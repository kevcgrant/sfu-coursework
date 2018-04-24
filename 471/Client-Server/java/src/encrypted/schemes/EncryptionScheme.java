package encrypted.schemes;

/**
 * CMPT 471 Assignment 4
 * <p>
 * Two-stage encryption scheme consisting of a substitution scheme and a transposition scheme.
 * There are 10 possible schemes of each type.
 *
 * @author Kevin Grant - 301192898
 *
 */
public class EncryptionScheme {
	
	/**
	 * The list of substitution keys. Each key results in a different scheme of substitution.
	 * The characters a-z, A-Z, 0-9, and the symbols !@#$%^&*()_-+=<>,.?/{}[]`|:;'" are all represented.
	 */
	private static final String[] SUBSTITUTION_KEYS = 
		{
				"U;0XeSMLqn'V>)42zY|_fPTiJ=r Cj:phF/5Qgy63H%!-ZOkl#E8@`dRvtGxB[9?Ac]\"oIW{}<Nw^D(u$1*K+,am.sb7&",
				"<r$;8xkq|%2OP5Jey(?oW@:uRcjEaQ-vmU`^A'\"[B)fG!zh9bg4#}1iwF.VL,Z60_]NtDl=p/&>3XKd7MI*+ THnS{sYC",
				"8^NMg:v=wDzQs?9xiIjYEB{,!$de/Ty_G+m@U&t\"]rlA1*ChW)bL5ZXu 7pKH`[0R|<%n;fac>3(6POF#Sk}4V.o-q'2J",
				"n4YGp$uB<K`?}PH:Le0sW{SoO =)('jbw|^%c#A8ixZ2rM.56D]3vfR&1zy!\"/QqJTlkF;V_N9[*gCE,7>UItahdm@+-X",
				"LnF@Ax.kdE%/HZzYq`2BeN$|4p8}6lG0: JQU5Kw]VbP(mO;W='I>R*vaX<o-7y!T3\"h)i+gu1_st?[9{CMf#j,D^cSr&",
				"LtSAEHo]823iTOXf:-B@VZa&#*/qd,6(;9gejPIm%xW!kM4?1FN^UR)5G| _pvzblu`c7yC.DY}'wJ[0=$h<+r>Qs{Kn\"",
				"flTWg(19Yp0sQqKybaEOXkF+L=Zd/B3HcAP<'}Mwe!-x,;)oCD5GrV.jz]|hu#:N_*{`mi\" 87RU%v&S[$2>?I6^4Jnt@",
				"w/h+JR63[b7zNuaUYG(^\"pKTcd VAeD>Ii%'$oHEMn@rCxv0l9|B;m-L#t!,_5f<sW2`4:=q]*g{j)ky.&8S}?1OXFQZP",
				"hI9Vx.OWEN*Qn<g8mY)'1zu{&adR!4:j-Fvl>L6\"0ofcCy_?i`Z[Se#bD/t%M5w3Pk,}^;pTK+XJ2( @qU]GH$7s|ABr=",
				"wkrF+Z;odYi`\"W/T,CJpt.BD{Oc]1me'}<0s(f-z&nu#AU?q)R>[QgVjvL@HaG2lby_$4N!=97hM6SE*%X| ^K:85xIP3"
		};
	
	/**
	 * The list of transposition keys. Each key specifies the order of columns to read to create the 
	 * transposed matrix, and the number of elements in the key is the number of columns.
	 */
	private static final int[][] TRANSPOSITION_KEYS = 
		{
				new int[] {4, 5, 1, 2, 0, 3},
				new int[] {0, 5, 9, 6, 3, 4, 1, 7, 2, 8},
				new int[] {2, 3, 4, 7, 0, 6, 5, 1},
				new int[] {3, 1, 0, 2},
				new int[] {6, 2, 1, 0, 4, 3, 5},
				new int[] {8, 2, 6, 5, 7, 4, 3, 1, 0},
				new int[] {1, 2, 3, 6, 5, 0, 4},
				new int[] {4, 5, 2, 1, 0, 3},
				new int[] {8, 2, 3, 7, 9, 1, 0, 6, 4, 5},
				new int[] {3, 4, 2, 1, 0}
		};
	
	private final SubstitutionScheme substitutionScheme;
	private final TranspositionScheme transpositionScheme;
	
	/**
	 * Initializes the substitution and transposition schemes.
	 * The scheme chosen, Si, is calculated by:
	 * <pre>
	 * i = privateKey mod 10
	 * </pre>
	 */
	public EncryptionScheme(int privateKey) {
		int keyIndex = (privateKey % SUBSTITUTION_KEYS.length);
		this.substitutionScheme = new SubstitutionScheme(SUBSTITUTION_KEYS[keyIndex]);
		this.transpositionScheme = new TranspositionScheme(TRANSPOSITION_KEYS[keyIndex]);
	}
	
	/**
	 * Encrypts the text through substitution and then transposition.
	 */
	public String encrypt(String plaintext) {
		String substitutedText = substitutionScheme.encrypt(plaintext);
		return transpositionScheme.encrypt(substitutedText);
	}

	/**
	 * Decrypts the text through transposition and then substitution.
	 */
	public String decrypt(String ciphertext) {
		String decryptedTransposition = transpositionScheme.decrypt(ciphertext);
		return substitutionScheme.decrypt(decryptedTransposition);
	}
}

// CMPT 383 Assignment 2
// Kevin Grant - 301192898

package main

import (
    "os"
    "io/ioutil"
    "fmt"
    "strings"
    "bytes"
    "unicode"
    "encoding/hex"
)

const ERROR_CODE = 1

func main() {
    b := checkAndReadFile()
    tokens := tokenize(string(b))
    colorize(tokens)
}

func checkAndReadFile() []byte {
    ensureFileIsProvided()
    file := os.Args[1]
    b, err := ioutil.ReadFile(file)
    if err != nil {
        println("Error: failed to read file!")
        os.Exit(ERROR_CODE)
    }
    return b
}

func ensureFileIsProvided() {
    if len(os.Args) < 2 {
        println("Error: no filename provided!")
        os.Exit(ERROR_CODE)
    }
}

// tokenize
// Basic implementation from http://www.cs.sfu.ca/CourseCentral/383/tjd/syntaxAndEBNF.html#examples
// ----------------------------------------------

// Token types
const (
    BRACE = iota
    BRACKET
    COLON
    COMMA
    CONDITIONAL
    STRING
    NUMBER
)

type Token struct {
    kind int
    val string
}

func tokenize(input string) (tokens []Token) {
    n := len(input)
    for i := 0; i < n; i++ {
        c := input[i]
        if c == '"' {
            str := tokenizeString(input, i)
            tokens = append(tokens, Token{STRING, str})
            i += len(str) - 1
        } else if isNumber(c) {
            number := getWord(input, i)
            tokens = append(tokens, Token{NUMBER, number})
            i += len(number) - 1
        } else if isBrace(c) {
            tokens = append(tokens, Token{BRACE, string(c)})
        } else if isBracket(c) {
            tokens = append(tokens, Token{BRACKET, string(c)})
        } else if c == ':' {
            tokens = append(tokens, Token{COLON, string(c)})
        } else if c == ',' {
            tokens = append(tokens, Token{COMMA, string(c)})
        } else if !isWhitespace(c) {
            conditional := getWord(input, i)
            tokens = append(tokens, Token{CONDITIONAL, conditional})
            i += len(conditional) - 1
        }
    }
    return tokens
}

func tokenizeString(input string, index int) string {
    var buffer bytes.Buffer
    buffer.WriteByte('"')
    for i := index + 1; i < len(input); i++ {
        c := input[i]
        buffer.WriteByte(c)
        isEndOfString := c == '"' && input[i - 1] != '\\'
        if isEndOfString {
            break
        }
    }
    return buffer.String()
}

func isNumber(c byte) bool {
    return c == '-' || unicode.IsDigit(rune(c))
}

func isBrace(char byte) bool {
    return char == '{' || char == '}'
}

func isBracket(char byte) bool {
    return char == '[' || char == ']'
}

func isWhitespace(c byte) bool {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r'
}

func getWord(input string, index int) string {
    var buffer bytes.Buffer
    for i := index; i < len(input); i++ {
        c := input[i]
        if c != ',' && !isWhitespace(c) && !isBracket(c) && !isBrace(c) {
            buffer.WriteByte(c)
        } else {
            break
        }
    }
    return buffer.String()
}

// Format
// ----------------------------------------------

const ESCAPE_COLOR = "rgb(194,24,7)"

var (
    tabLevel int
    escapeCharacters string = "nbfrt'/\"\\"
    colors = map[int] string {
        BRACE : "rgb(70, 130, 180)",
        BRACKET : "rgb(100, 149, 237)",
        COLON : "black",
        COMMA : "green",
        CONDITIONAL : "salmon",
        STRING : "blue",
        NUMBER : "purple",
    }
)

func colorize(tokens []Token) {
    tabLevel = 0
    fmt.Printf("<span style=\"font-family:monospace; white-space:pre\">")
    for _, t := range tokens {
        fmt.Printf(getColoredStringForToken(t))
    }
    fmt.Printf("</span>")
}

func getColoredStringForToken(t Token) string {
    if t.kind == STRING {
        t.val = replaceSpecialSymbols(t.val)
        t.val = colorizeEscapeCharacters(t.val)
    }
    t.val = formatWhitespace(t)
    return addSpanTagsToString(colors[t.kind], t.val)
}

func addSpanTagsToString(color, val string) string {
    return "<span style=\"color:" + color + "\">" + val + "</span>"
}

func replaceSpecialSymbols(s string) string {
    s = strings.Replace(s, "&", "&amp;", -1)
    s = strings.Replace(s, "<", "&lt;", -1)
    s = strings.Replace(s, ">", "&gt;", -1)
    s = strings.Replace(s, "\"", "&quot;", -1)
    s = strings.Replace(s, "'", "&apos;", -1)
    s = strings.Replace(s, "\\&quot;", "\\\"", -1) // leave escaped " as is
    s = strings.Replace(s, "\\&apos;", "\\'", -1) // leave escaped ' as is
    return s
}

func colorizeEscapeCharacters(val string) string {
    for i := 0; i < len(val) - 1; i++ {
        if val[i] == '\\' {
            var seq string = ""
            if strings.Contains(escapeCharacters, string(val[i + 1])) {
                seq = val[i : i+2]
            } else if val[i + 1] == 'u' {
                if i < len(val) - 5 && isHexSequence(val[i+2 : i+6]) {
                    seq = val[i : i+6]
                }
            }
            if seq != "" {
                spannedEscape := addSpanTagsToString(ESCAPE_COLOR, seq)
                val = val[:i] + spannedEscape + val[i + len(seq):]
                i += len(spannedEscape) - len(seq)
            }
        }
    }
    return val
}

func isHexSequence(s string) bool {
    _, err := hex.DecodeString(s)
    return err == nil
}

func formatWhitespace(t Token) string {
    if t.val == "{" || t.val == "[" {
        tabLevel++
        t.val += "\n" + getTabs()
    } else if t.val == "," {
        t.val += "\n" + getTabs()
    } else if t.val == "]" || t.val == "}"  {
        tabLevel--
        t.val = "\n" + getTabs() + t.val
    } else if t.val == ":" {
        t.val = " " + t.val + " "
    }
    return t.val
}

func getTabs() string {
    var buffer bytes.Buffer
    for i := 0; i < tabLevel; i++ {
        buffer.WriteString("    ")
    }
    return buffer.String()
}

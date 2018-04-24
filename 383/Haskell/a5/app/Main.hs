-- CMPT 383 Assignment 5
-- Kevin Grant // 301192898

------------------------------------------------------------------------------------------
-- 1

-- returns a new list that is the same as lst, except x has been added to the end of it
snoc :: a -> [a] -> [a]
snoc v []     = [v]
snoc v (x:xs) = x:snoc v xs


------------------------------------------------------------------------------------------
-- 2

-- Own version of the Haskell append operator ++
myappend :: [a] -> [a] -> [a]
myappend s t
  | null s    = t
  | null t    = s
  | otherwise = myappend (snoc (head t) s) (tail t)


------------------------------------------------------------------------------------------
-- 3

-- Own version of reverse
myreverse :: [a] -> [a]
myreverse lst
  | null lst      = []
  | singleton lst = lst
  | otherwise     = myappend (myreverse (tail lst)) [head lst]

-- Tests whether a list has only one element
singleton :: [a] -> Bool
singleton lst = length lst == 1


------------------------------------------------------------------------------------------
-- 4

-- Returns the number of emirps less than, or equal to, n.
count_emirps :: Int -> Int
count_emirps n
  | n < 13    = 0
  | isEmirp n = 1 + count_emirps (n - 1)
  | otherwise = count_emirps (n - 1)

-- Returns true if n is an emirp
isEmirp :: Int -> Bool
isEmirp n = n /= r && isPrime n && isPrime r
            where r = reverseInt n

-- Reverses a given integer
-- https://stackoverflow.com/questions/19725292/how-to-reverse-an-integer-in-haskell
reverseInt :: Int -> Int
reverseInt x = read . reverse . show $ x

-- Returns true if n is prime
isPrime :: Int -> Bool
isPrime n
  | n < 2     = False
  | otherwise = smallestDivisor n == n

-- Returns the smallest divisor of n
smallestDivisor :: Int -> Int
smallestDivisor n
  | n == 0    = 0
  | n == 1    = 1
  | otherwise = head (dropWhile (\x -> n `mod` x /= 0) [2..n])


------------------------------------------------------------------------------------------
-- 5

-- Returns the list with the greatest sum
biggest_sum :: [[Int]] -> [Int]
biggest_sum lst = greatest sum lst


------------------------------------------------------------------------------------------
-- 6

-- Returns the item in seq that maximizes function f
greatest :: (a -> Int) -> [a] -> a
greatest f seq
  | singleton seq = head seq
  | otherwise     = greater f (head seq) (greatest f (tail seq))

greater :: (a -> Int) -> a -> a -> a
greater f a b
  | f a > f b = a
  | otherwise = b


------------------------------------------------------------------------------------------
-- 7

-- Returns True when x is 0 or 1, and False otherwise
is_bit :: Int -> Bool
is_bit x = x == 0 || x == 1


------------------------------------------------------------------------------------------
-- 8

flip_bit :: Int -> Int
flip_bit x
  | x == 0    = 1
  | x == 1    = 0
  | otherwise = error ("Bit must be either 0 or 1, is " ++ show x)


------------------------------------------------------------------------------------------
-- 9

-- Returns True if x is the empty list, or if it contains only bits (as determined by is_bit)
-- a

is_bit_seq1 :: [Int] -> Bool
is_bit_seq1 seq
  | null seq      = True
  | singleton seq = is_bit (head seq)
  | otherwise     = is_bit (head seq) && is_bit_seq1 (tail seq)

-- b

is_bit_seq2 :: [Int] -> Bool
is_bit_seq2 []  = True
is_bit_seq2 seq = if singleton seq then is_bit (head seq)
                  else is_bit (head seq) && is_bit_seq1 (tail seq)

-- c

is_bit_seq3 :: [Int] -> Bool
is_bit_seq3 seq = all (\x -> is_bit x) seq


------------------------------------------------------------------------------------------
-- 10

-- Returns a sequence of bits that is the same as x, except 0s become 1s and 1s become 0s
-- a

invert_bits1 :: [Int] -> [Int]
invert_bits1 seq
  | null seq      = seq
  | singleton seq = [flip_bit (head seq)]
  | otherwise     = [flip_bit (head seq)] ++ invert_bits1 (tail seq)

-- b

invert_bits2 :: [Int] -> [Int]
invert_bits2 seq = map (\x -> flip_bit x) seq

-- c

invert_bits3 :: [Int] -> [Int]
invert_bits3 seq = [flip_bit n | n <- seq]


------------------------------------------------------------------------------------------
-- 11

-- Returns a pair of values indicating the number of 0s and 1s in x
bit_count :: [Int] -> (Int, Int)
bit_count seq
  | null seq      = (0, 0)
  | singleton seq = if (head seq) == 0 then (1, 0) else (0, 1)
  | otherwise     = sumPairs (bit_count [head seq]) (bit_count (tail seq))

-- Sums together two pairs, e.g. sumPairs (1,2) (3,4) = (4,6)
sumPairs :: (Int, Int) -> (Int, Int) -> (Int, Int)
sumPairs a b = (fst a + fst b, snd a + snd b)


------------------------------------------------------------------------------------------
-- 12

-- Returns a list of all bit sequences of length n
all_basic_bit_seqs :: Int -> [[Int]]
all_basic_bit_seqs n
  | n < 1     = []
  | otherwise = [bit_seq x n | x <- [0..(2^n - 1)]]

-- Returns the bit sequence for integer n of the given length
bit_seq :: Int -> Int -> [Int]
bit_seq n length
  | length - 1 < 0    = []
  | n >= 2^(length - 1) = [1] ++ bit_seq (n - 2^(length - 1)) (length - 1)
  | otherwise     = [0] ++ bit_seq n (length - 1)


------------------------------------------------------------------------------------------
-- 13

data Bit = Zero | One
    deriving (Show, Eq)

-- Changes a Zero to a One, and vice-versa
flipBit :: Bit -> Bit
flipBit b = if b == One then Zero else One


------------------------------------------------------------------------------------------
-- 14

-- Flips all the bits in the input list
invert :: [Bit] -> [Bit]
invert bits = [flipBit n | n <- bits]


------------------------------------------------------------------------------------------
-- 15

-- Returns a list of Bit lists representing all possible bit sequences of length n
all_bit_seqs :: Int -> [[Bit]]
all_bit_seqs n = [bitSeq x n | x <- [0..(2^n - 1)]]

-- Returns the Bit sequence for integer n of the given length
-- list comprehension says: value of Bit at position i for int n is -> floor(n / 2^i) mod 2
bitSeq :: Int -> Int -> [Bit]
bitSeq n length = [bitValue (floor (realToFrac n / 2^i) `mod` 2) | i <- [length - 1, (length - 2)..0]]

-- Returns the Bit value of the integer bit
bitValue :: Int -> Bit
bitValue x = if x == 1 then One else Zero


------------------------------------------------------------------------------------------
-- 16

-- Returns the sum of the bits in the input where Zero is 0, and One is 1
bitSum1 :: [Bit] -> Int
bitSum1 bits = sum [intValue n | n <- bits]

-- Returns the int value of the Bit
intValue :: Bit -> Int
intValue x = if x == One then 1 else 0


------------------------------------------------------------------------------------------
-- 17

-- Returns the sum of the maybe-bits in the input
bitSum2 :: [Maybe Bit] -> Int
bitSum2 bits = sum [maybeBitIntValue n | n <- bits]

-- Returns the int value of the maybe bit
maybeBitIntValue :: Maybe Bit -> Int
maybeBitIntValue x = if x == Just One then 1 else 0


------------------------------------------------------------------------------------------
-- 18

data List a = Empty | Cons a (List a)
    deriving Show

-- Converts a regular Haskell list to a List a
toList :: [a] -> List a
toList s
  | null s      = Empty
  | otherwise   = Cons (head s) (toList (tail s))


------------------------------------------------------------------------------------------
-- 19

-- Converts a List a to a regular Haskell list
toHaskellList :: List a -> [a]
toHaskellList Empty             = []
toHaskellList (Cons first rest) = [first] ++ toHaskellList rest


------------------------------------------------------------------------------------------
-- 20

-- Returns a new List a that consists of all the elements of A followed by all the elements of B
append :: List a -> List a -> List a
append Empty b          = b
append a Empty          = a
append (Cons a Empty) b = Cons a b
append a b              = append (allButLast a) (append (lastList a) b)

-- Returns the last item in the List a, e.g. (Cons 1 (Cons 2 Empty)) -> Cons 2 Empty
lastList :: List a -> List a
lastList Empty              = Empty
lastList (Cons first Empty) = Cons first Empty
lastList (Cons _ rest)      = lastList rest

-- Returns all items except the last one in the List a, e.g. (Cons 1 (Cons 2 (Cons 3 Empty))) -> Cons 1 (Cons 2 Empty)
allButLast :: List a -> List a
allButLast Empty             = Empty
allButLast (Cons _ Empty)    = Empty
allButLast (Cons first rest) = Cons first (allButLast rest)


------------------------------------------------------------------------------------------
-- 21

-- Returns a List a that is the same as L but all items satisfying f (i.e. for which f returns True) have been removed
removeAll :: (a -> Bool) -> List a -> List a
removeAll _ Empty             = Empty
removeAll f (Cons first rest) = if f first then removeAll f rest
                                else Cons first (removeAll f rest)


------------------------------------------------------------------------------------------
-- 22

-- Returns a new List a equal to L in ascending order (QuickSort)
sort :: Ord a => List a -> List a
sort Empty             = Empty
sort (Cons first rest) = append (append smalls (Cons first Empty)) bigs
                         where smalls = sort (removeAll (\x -> x > first) rest)
                               bigs   = sort (removeAll (\x -> x <= first) rest)

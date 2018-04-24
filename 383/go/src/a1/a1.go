// CMPT 383 Assignment 1
// Kevin Grant - 301192898

package a1

import (
    "fmt"
    "strings"
    "math"
    "io/ioutil"
    "errors"
    "reflect"
)

// QUESTION 1
// -------------------------------------------------------------

// Returns the number of primes less than, or equal to, the int n
func countPrimes(n int) int {
    if n < 2 {
        return 0
    }
    primeCount := 0
    for i := 2; i <= n; i++ {
        if isPrime(i) {
            primeCount++
        }
    }
    return primeCount
}

func isPrime(x int) bool {
    for i := 2; i < int(math.Sqrt(float64(x))) + 1; i++ {
        if x % i == 0 {
            return false
        }
    }
    return true;
}

// QUESTION 2
// -------------------------------------------------------------

func countStrings(filename string) map[string]int {
    bytes, ok := ioutil.ReadFile(filename)
    if ok != nil {
        panic("Failed to read file")
    }
    contents := string(bytes)
    counts := make(map[string]int)
    for _, word := range strings.Fields(contents) {
        counts[word] = counts[word] + 1;
    }
    return counts
}

// QUESTION 3
// -------------------------------------------------------------

// 0 <= hour < 24
// 0 <= minute < 60
// 0 <= second < 60
type Time24 struct {
    hour, minute, second uint8
}

// Return the number of seconds in the Time24 object
func (t Time24) Seconds() int {
    return int(t.second) + (int(t.minute)*60) + (int(t.hour)*3600) 
}

// Returns true if a and b are exactly the same time, and false otherwise
func equalsTime24(a, b Time24) bool {
    return a.Seconds() == b.Seconds()
}

// Returns true, if time a comes strictly before b, and false otherwise.
func lessThanTime24(a, b Time24) bool {
    return a.Seconds() < b.Seconds()
}

// Converts a Time24 object to a human-readable string. 
// Ref: http://joncarlmatthews.com/2016/05/21/go-lang-prepend-leading-zero-before-single-digit-number/
func (t Time24) String() string {
    hours := fmt.Sprintf("%02d", t.hour)
    minutes := fmt.Sprintf("%02d", t.minute)
    seconds := fmt.Sprintf("%02d", t.second)
    return hours + ":" + minutes + ":" + seconds
}

// Returns true if t is a valid Time24 object
func (t Time24) validTime24() bool {
    return t.hour < 24 && t.minute < 60 && t.second < 60 &&
           t.hour >= 0 && t.minute >= 0 && t.second >= 0
}

// Returns the smallest time in a slice of Time24 objects
func minTime24(times []Time24) (Time24, error) {
    if (len(times) < 1) {
        return Time24{0, 0 ,0}, errors.New("Input is empty - returning smallest possible Time")
    }
    min := Time24{23, 59, 59}
    for _, v := range times {
        if lessThanTime24(v, min) {
            min = v
        }
    }
    return min, nil
}


// QUESTION 4
// -------------------------------------------------------------

// Uses linear search to return the first index location of x in the slice lst
// Ref (ranging over interface): http://stackoverflow.com/a/14026030
func linearSearch(x interface{}, lst interface{}) int {
    panicIfInvalidTypes(x, lst)
    slice := reflect.ValueOf(lst)
    for i := 0; i < slice.Len(); i++ {
        if x == slice.Index(i).Interface() {
            return i
        }
    }
    return -1
}

func panicIfInvalidTypes(x interface{}, lst interface{}) {
    if reflect.TypeOf(lst).Kind() != reflect.Slice {
        panic("lst must be a slice!")
    }
    typeX, typeLst := reflect.TypeOf(x), reflect.TypeOf(lst).Elem()
    if typeX != typeLst {
        panic(fmt.Sprintf("Inconsistent types: x is type %v and lst element is type %v", typeX, typeLst))
    }
}

// QUESTION 5
// -------------------------------------------------------------

// Returns all the bit sequences of length n.
func allBitSeqs(n int) [][]int {
    if (n < 1) {
        return make([][]int, 0)
    }
    numSequences := 1 << uint(n)
    seqs := make([][]int, numSequences)
    for i := 0; i < numSequences; i++ {
        seqs[i] = getBitSeqForInt(i, n)
    }
    return seqs
}

// Get the bit sequence for a particular integer.
// Ref: http://stackoverflow.com/a/9026235
func getBitSeqForInt(val, bitCount int) []int {
    bits := make([]int, bitCount)
    for j := 0; j < bitCount; j++ {
        if val & (1 << uint(j)) > 0 {
            bits[bitCount - j - 1] = 1
        } else {
            bits[bitCount - j - 1] = 0
        }
    }
    return bits
}



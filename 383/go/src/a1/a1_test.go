// CMPT 383 Assignment 1
// Kevin Grant - 301192898

package a1

import (
    "testing"
    "reflect"
    "errors"
)

// QUESTION 1
// -------------------------------------------------------------

func TestCountPrimes(t *testing.T) {
    var cases = map[int]int {
        0: 0,
        2: 1,
        5: 3,
        -5: 0,
        10000: 1229,
    }

    for k, v := range cases {
        result := countPrimes(k)
        if result != v {
            t.Errorf("Input %d failed: Expected=%d, Actual=%d", k, v, result)
        }
    }
}

// QUESTION 2
// -------------------------------------------------------------

// Ref: https://github.com/golang/tour/blob/master/wc/wc.go#L28 for basic implementation
func TestCountStrings(t *testing.T) {
    var cases = []struct {
        in string
        want map[string] int
    } {
        { "textfiles/bigdog.txt", map[string] int {
            "The":1, "the":1, "big":3, "dog":1, "ate":1, "apple":1,
        }},
        { "textfiles/empty.txt", map[string] int {},
        },
    }
    for _, c := range cases {
        valid := true
        result := countStrings(c.in)
        if len(result) != len(c.want) {
            valid = false
        } else {
            for k := range c.want {
                if c.want[k] != result[k] {
                    valid = false
                }
            }
        }
        if !valid {
            t.Errorf("Input %s failed: \n Expected=\n  %#v\n Actual:\n  %#v", c.in, result, c.want)
        }
    }
}

// QUESTION 3
// -------------------------------------------------------------

func TestEqualsTime24(t *testing.T) {
    var cases = []struct {
        a, b Time24
        want bool
    } {
        { Time24 {12, 0, 0}, Time24 {12, 0, 0}, true},
        { Time24 {0, 0, 0}, Time24 {0, 0, 0}, true },
        { Time24 {23, 0, 1}, Time24 {23, 0, 2}, false },
        { Time24 {10, 0, 0}, Time24 {11, 0, 0}, false },
        { Time24 {0, 1, 0}, Time24 {0, 2, 0}, false},
    }
    for _, c := range cases {
        result := equalsTime24(c.a, c.b)
        if result != c.want {
            t.Errorf("Input %v and %v failed: \n Expected=%v \n Actual=%v", c.a, c.b, c.want, result)
        }
    }
}

func TestLessThanTime24(t *testing.T) {
    var cases = []struct {
        a, b Time24
        want bool
    } {
        { Time24 {12, 0, 0}, Time24 {12, 0, 0}, false},
        { Time24 {0, 0, 0}, Time24 {0, 0, 0}, false },
        { Time24 {23, 0, 1}, Time24 {23, 0, 2}, true },
        { Time24 {10, 0, 0}, Time24 {11, 0, 0}, true },
        { Time24 {0, 1, 0}, Time24 {0, 2, 0},true},
        { Time24 {23, 11, 58}, Time24 {23, 11, 59}, true},
    }

     for _, c := range cases {
        result := lessThanTime24(c.a, c.b)
        if result != c.want {
            t.Errorf("Input %v and %v failed: \n Expected=%v \n Actual=%v", c.a, c.b, c.want, result)
        }
    }
}

func TestTime24String(t *testing.T) {
    var cases = map[Time24]string {
        {12, 0, 0} : "12:00:00",
        {13, 12, 11} : "13:12:11",
        {1, 1, 1} : "01:01:01",
        {23, 1, 59} : "23:01:59",
        {18, 45, 2} : "18:45:02",
    }
    for k, v := range cases {
        result := k.String()
        if result != v {
            t.Errorf("Input %v failed: Expected=%s, Actual=%s", k, v, result)
        }
    }
}

func TestValidTime24(t *testing.T) {
    var cases = map[Time24]bool {
        {0, 0, 0} : true,
        {23, 59, 59} : true,
        {12, 10, 8} : true,
        {24, 12, 8} : false,
        {10, 61, 3} : false,
        {10, 34, 61} : false,
        {43, 23, 34} : false,
        {23, 01, 65} : false,
    }
    for k, v := range cases {
        result := k.validTime24()
        if result != v {
            t.Errorf("Input %v failed: Expected=%v, Actual=%v", k, v, result)
        }
    }
}

func TestMinTime24(t *testing.T) {
    var cases = []struct {
        in []Time24 
        want Time24
        ok error
    } {
        { []Time24 {}, Time24{0, 0 ,0}, errors.New("") },
        { []Time24 {{0, 0, 1}, Time24{0, 0 ,0}}, Time24{0, 0 ,0}, nil },
        { []Time24 {{12, 11, 10}, {11, 10, 56}}, Time24 {11, 10, 56}, nil},
        { []Time24 {{23, 59, 59}, {23, 59, 59}}, Time24 {23, 59, 59}, nil},
    }
    for _, c := range cases {
        result, ok := minTime24(c.in)
        if result != c.want {
            t.Errorf("Input %v failed: Expected=%v, Actual=%v", c.in, c.want, result)
        }
        if (ok != nil && c.ok == nil) || (ok == nil && c.ok != nil) {
            t.Errorf("Input %v failed: Expected error=%v, Actual error=%v", c.in, c.ok, ok)
        }
    }
}

// QUESTION 4
// -------------------------------------------------------------

type LinearSearchParams struct {
        x interface{}
        lst interface{}
        want int
}

func TestLinearSearch(t *testing.T) {
    const panicCode = -2
    var cases = []LinearSearchParams {
        {5, []int{4, 2, -1, 5, 0}, 3},
        {3, []int{4, 2, -1, 5, 0}, -1},
        {"egg", []string{"cat", "nose", "egg"}, 2},
        {"up", []string{"cat", "nose", "egg"}, -1},
        {"up", []int{1, 2, 3}, panicCode},
        {1, []string{}, panicCode},
        {"orange", []string{}, -1},
    }

    for _, c := range cases {
        defer catchLinearSearchPanic(t, panicCode, c)
        result := linearSearch(c.x, c.lst)
        if result != c.want {
            t.Errorf("Input %v, %v failed: Expected=%v, Actual=%v", c.x, c.lst, c.want, result)
        }
        if c.want == panicCode {
            t.Errorf("Input %v, %v should have panicked, but it didn't!", c.x, c.lst)
        }
    }
}

func catchLinearSearchPanic(t *testing.T, panicCode int, p LinearSearchParams) {
    if r := recover(); r != nil && p.want != panicCode {
        t.Errorf("Input %v, %v panicked when it shouldn't have", p.x, p.lst)
    }
}

// QUESTION 5
// -------------------------------------------------------------

func TestAllBitSeqs(t *testing.T) {
    var cases = map[int][][]int {
            0: {},
            1: {{0}, {1}},
            2: {{0, 0}, {0, 1}, {1, 0}, {1, 1}},
            3: {{0, 0, 0}, {0, 0, 1}, {0, 1, 0}, {0, 1, 1}, {1, 0, 0}, {1, 0, 1}, {1, 1, 0}, {1, 1, 1}},
    }
    for k, v := range cases {
        result := allBitSeqs(k)
        if !reflect.DeepEqual(result, v) {
            t.Errorf("Input %v failed: Expected=%v, Actual=%v", k, v, result)
        }
    }
}


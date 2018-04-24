// CMPT 383 Assignment 2
// Kevin Grant - 301192898

package main

import (
    "testing"
    "reflect"
    "io/ioutil"
)

func TestScanner(t *testing.T) {
    var cases = map[string][]Token {
        "jsonfiles/test1.json" : {
            {BRACE, "{"},
            {STRING, "\"key\""}, {COLON, ":"}, {STRING, "\"\\u8a3eval1\""}, {COMMA, ","},
            {STRING, "\"key2\""}, {COLON, ":"}, {NUMBER, "1231"}, {COMMA, ","},
            {STRING, "\"list\""}, {COLON, ":"}, {BRACKET, "["}, {NUMBER, "-10"}, {COMMA, ","}, {NUMBER, "3"}, {COMMA, ","}, {NUMBER, "12.12"}, {COMMA, ","}, {NUMBER, "32.1e23"}, {COMMA, ","}, {NUMBER, "10e-12"}, {BRACKET, "]"}, {COMMA, ","},
            {STRING, "\"item\""}, {COLON, ":"}, {BRACE, "{"},
            {STRING, "\"thing1\""}, {COLON, ":"}, {STRING, "\"asd\\n\\r\\\"\""}, {COMMA, ","},
            {STRING, "\"thing2\""}, {COLON, ":"}, {CONDITIONAL, "true"}, {COMMA, ","},
            {STRING, "\"true\""}, {COLON, ":"}, {CONDITIONAL, "false"},
            {BRACE, "}"}, {COMMA, ","},
            {STRING, "\"null_\""}, {COLON, ":"}, {CONDITIONAL, "null"}, {COMMA, ","},
            {STRING, "\"n\""}, {COLON, ":"}, {STRING, "\"123\""},
            {BRACE, "}"},
        },
    }

    for k, v := range cases {
        bytes, _ := ioutil.ReadFile(k)
        defer catchTokenizePanic(t)
        tokens := Tokenize(string(bytes))
        if !reflect.DeepEqual(tokens, v) {
            t.Errorf("Expected: \n %v \n\n Actual: \n %v \n", v, tokens)
        }
    }
}

func catchTokenizePanic(t *testing.T) {
    if r := recover(); r != nil {
        t.Errorf("Invalid char!")
    }
}
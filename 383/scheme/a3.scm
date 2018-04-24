;;; CMPT 383 Assignment 3
;;; Kevin Grant / 301192898


;;; 1 ------------------------------------------------------------------------

;;; (my-last lst) returns the last element of lst
(define my-last
    (lambda (lst)
        (cond
            ((null? lst) 
                (error "my-last: empty list"))
            ((= 1 (length lst)) 
                (car lst))
            (else 
                (my-last (cdr lst)))
        )
    )
)

;;; 2 ------------------------------------------------------------------------

;;; (snoc x lst) returns a list that is the same as lst except x has been added to the right end of the list
(define snoc
    (lambda (x lst)
        (cond
            ((null? lst)
                (cons x '()))
            (else
                (cons (car lst) (snoc x (cdr lst))))
        )
    )
)

;;; 3 ------------------------------------------------------------------------

;;; (range n) returns the list (0 1 2 ... n-1) 
(define range
    (lambda (n)
        (cond
            ((positive? n) 
                (first-n-ints n '()))
            (else 
                '())
        )
    )
)

(define first-n-ints
    (lambda (n lst)
        (cond
            ((zero? n) 
                lst)
            (else 
                (first-n-ints (- n 1) (cons (- n 1) lst)))
        )
    )
)

;;; 4 ------------------------------------------------------------------------

;;; (deep-sum lst) returns the sum of all the numbers in lst, including numbers within lists
;;; reference: http://www.cs.sfu.ca/CourseCentral/383/tjd/scheme-intro.html#more-examples (deep-count-num)
(define deep-sum
    (lambda (lst)
        (cond
            ((null? lst)
                0)
            ((list? (car lst))
                (+ (deep-sum (car lst)) (deep-sum (cdr lst))))
            ((number? (car lst))
                (+ (car lst) (deep-sum (cdr lst))))
            (else
                (deep-sum (cdr lst)))
        )
    )
)

;;; 5 ------------------------------------------------------------------------

;;; (count-primes n) returns the number of primes less than, or equal to, n
(define count-primes 
    (lambda (n)
        (cond
            ((< n 2) 
                0)
            (else
                (primes-in-range 3 n '(2)))
        )
    )
)

;;; (primes-in-range s t lst) helper function for count-primes that retains a list of the primes found thus far.
(define primes-in-range
    (lambda (s t lst)
        (cond
            ((> s t) 
                1)
            ((and (odd? s) (none-divide s lst))
                (+ 1 (primes-in-range (+ 1 s) t (snoc s lst))))
            (else
                (primes-in-range (+ 1 s) t lst))
        )
    )
)

;;; (none-divide n lst) returns true if none of the numbers in lst divide n.
(define none-divide
    (lambda (n lst)
        (or (null? lst)
            (and 
                (not (integer? (/ n (car lst)))) 
                (none-divide n (cdr lst))
            )
        )
    )
)

;;; 6 ------------------------------------------------------------------------

;;; (is-bit? x) returns #t when x is the number 0 or 1, and #f otherwise.
(define is-bit? 
    (lambda (x)
        (and
            (number? x)
            (<= 0 x 1)
        )
    )   
)

;;; 7 ------------------------------------------------------------------------

;;; (is-bit-seq? lst) returns true if lst is the empty list, or if it contains only bits
(define is-bit-seq?
    (lambda (lst)
        (or
            (null? lst)
            (and 
                (is-bit? (car lst)) 
                (is-bit-seq? (cdr lst))
            )
        )
    )
)

;;; 8 ------------------------------------------------------------------------

;;; (all-bit-seqs n) returns a list of all the bit sequences of length n
(define all-bit-seqs 
    (lambda (n)
        (let ((num-seqs (expt 2 n)))
            (cond
                ((positive? n)
                    (map (lambda (x) (bit-seq x (- n 1) n '())) (range num-seqs)))
                (else 
                    '())
            )
        )
    )
)

;;; (bit-seq n i numBits lst) returns the bit sequence for the integer n
;;; i is the counter for the exponent (2^i) that should initially be (- numBits 1)
(define bit-seq 
    (lambda (n i numBits lst)
        (cond 
            ((negative? i)
                lst)
            ((>= n (expt 2 i))
                (bit-seq (- n (expt 2 i)) (- i 1) numBits (snoc 1 lst)))
            (else
                (bit-seq n (- i 1) numBits (snoc 0 lst)))
        )
    )
)




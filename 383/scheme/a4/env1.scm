;;; CMPT 383 Assignment 4
;;; Kevin Grant / 301192898

;;; env1 is represented as pair of two lists, with the first list containing the 
;;; variables and the second list containing the values, where vars(i) = vals(i).


;;; Returns a new empty environment
(define make-empty-env
    (lambda ()
        '(() ())
    )
)

;;; Returns the value of variable v in environment env
(define apply-env
    (lambda (env v)
        (let ((v-index (var-index env v)))
            (if (not (negative? v-index))
                (list-ref (vals env) v-index)
                (error (string "apply-env: unknown variable " v))
            )
        )
    )
)

;;; Returns a new environment that is the same as env except that the value of v in it is val
(define extend-env
    (lambda (v val env)
        (let ((v-index (var-index env v)))
            (if (not (negative? v-index))
                (replace-in-env env v-index v val)
                (list (cons v (variables env)) (cons val (vals env)))
            )
        )
    )
)

;;; Return the variables for this environment
(define variables
    (lambda (env)
        (car env)
    )
)

;;; Return the values for this environment
(define vals
    (lambda (env)
        (cadr env)
    )
)


;;; Returns the index of the variable in the environment, or -1 if not found
(define var-index
    (lambda (env var)
        (var-index-lst (variables env) var 0)
    )
)

(define var-index-lst
    (lambda (lst var i)
        (if (null? lst)
            -1
            (if (equal? (car lst) var)
                i
                (var-index-lst (cdr lst) var (+ 1 i))
            )
        )
    )
)

;; Replace the variable at position i in the environment with v, and the value at position i with val
(define replace-in-env
    (lambda (env i v val)
        (list (replace-at (variables env) i v) (replace-at (vals env) i val))
    )
)

;; Returns a list with the item at index i replaced with val
(define replace-at
    (lambda (lst i val)
        (cond 
            ((null? lst)
                '())
            ((zero? i)
                (cons val (cdr lst)))
            (else
                (cons (car lst) (replace-at (cdr lst) (- i 1) val)))
        )
    )
)
;;; CMPT 383 Assignment 4
;;; Kevin Grant / 301192898

;;; env2 utilizes closures. the environment itself is represented as a function, which 
;;; initially returns an error (because no variables are bound). For each call to extend-env,
;;; a new closure is created, which binds its variable, value, and old env. When called, if the 
;;; v parameter matches its bound variable, it returns the value, otherwise it delegates to the
;;; old env.
;;; This essentially creates a delegation chain. 

;;; Returns a new empty environment
(define make-empty-env
    (lambda ()
        (lambda (v)
            (error (string "apply-env: unknown variable " v))
        )
    )
)

;;; Returns the value of variable v in environment env
(define apply-env
    (lambda (env v)
        (env v)
    )
)

;;; Returns a new environment that is the same as env except that the value of v in it is val
(define extend-env
    (lambda (v val env)
        (lambda (var)
            (if (equal? v var)
                val
                (env var)
            )
        )
    )
)
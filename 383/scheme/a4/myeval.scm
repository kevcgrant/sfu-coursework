;;; CMPT 383 Assignment 4
;;; Kevin Grant / 301192898

;;; evaluates the infix expression expression expr in the environment env
(define myeval
	(lambda (expr env)
		(if (is-expression? expr)
			(evaluate expr env)
			(error "is-expression: expression provided is not valid")
		)
	)
)

;;; Returns true if the expression is valid, false otherwise
(define is-expression?
	(lambda (expr)
		(if (or (symbol? expr) (number? expr))
			#t
			(if (list? expr)
				(cond 
					((is-operation? expr)
						(and (is-expression? (first expr)) (is-operator? (second expr)) (is-expression? (third expr))))
					((is-func? expr)
						(and (is-func? (first expr)) (is-expression? (second expr))))
					(else
						#f)
				)
				#f
			)
		)
	)
)

(define is-operation?
	(lambda (expr)
		(= 3 (length expr))
	)
)

(define is-func?
	(lambda (expr)
		(= 2 (length expr))
	)
)

(define is-operator?
	(lambda (x)
		(let ((ops '(+ - * / **)))
			(if (memq x ops)
				#t #f
			)
		)
	)
)

(define is-func?
	(lambda (x)
		(or (equal? x 'inc) (equal? x 'dec))
	)
)

;;; basic implementation: http://www.cs.sfu.ca/CourseCentral/383/tjd/scheme-intro.html#an-expression-evaluator
(define evaluate
    (lambda (e env)
        (cond
            ((number? e)
                e)
            ((symbol? e)
            	(apply-env env e))
    		((is-operation? e)
                (evaluate-op e env))
    		((is-func? e)
				(evaluate-func e env))
		)
	)
)

(define evaluate-op
	(lambda (e env)
		(let 
        	((left (evaluate (car e) env))
              (op (car (cdr e)))
              (right (evaluate (car (cdr (cdr e))) env))
        	)
            (cond
                ((equal? op '+) (+ left right))
                ((equal? op '-) (- left right))
                ((equal? op '*) (* left right))
                ((equal? op '/) (safe-divide left right))
                ((equal? op '**) (expt left right))
			)
		)
	)
)

(define safe-divide
	(lambda (x y)
		(if (zero? y)
			(error "safe-divide: cannot divide by zero!")
			(/ x y)
		)
	)
)

(define evaluate-func
	(lambda (e env)
		(let 
			((func (car e))
			 (expr (evaluate (cadr e) env))
			)
			(if (equal? func 'inc)
				(+ 1 expr)
				(- 1 expr)
			)
		)
	)
)
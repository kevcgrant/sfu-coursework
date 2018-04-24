;;; CMPT 383 Assignment 4
;;; Kevin Grant / 301192898

(define simplify
	(lambda (expr)
		(cond
			((or (number? expr) (symbol? expr))
				expr)
			((is-operation? expr)
				(simplify-op expr))
			((is-func? expr)
				(simplify-func expr))
		)
	)
)

(define simplify-op
	(lambda (expr)
		(let 
			(
				(left (simplify (car expr)))
				(right (simplify (cadr (cdr expr))))	
			)
			(cond
				((is-addition? expr)
					(simplify-addition left right))
				((is-subtraction? expr)
					(simplify-subtraction left right))
				((is-multiplication? expr)
					(simplify-multiplication left right))
				((is-division? expr)
					(simplify-division left right))
				((is-exponent? expr)
					(simplify-exponent left right))
				(else
					expr)
			)
		)
	)
)

(define simplify-addition
	(lambda (left right)
		(cond
			((safe-zero? left)
				right)
			((safe-zero? right)
				left)
			((no-variables? left right)
				(+ left right))
			(else
				(list left '+ right))
		)
	)
)

(define simplify-subtraction
	(lambda (left right)
		(cond
			((safe-zero? right)
				left)
			((equal? left right)
				0)
			((no-variables? left right)
				(- left right))
			(else
				(list left '- right))
		)
	)
)

(define simplify-multiplication
	(lambda (left right)
		(cond
			((or (safe-zero? left) (safe-zero? right))
				0)
			((equal? 1 left)
				right)
			((equal? 1 right)
				left)
			((no-variables? left right)
				(* left right))
			(else
				(list left '* right))
		)
	)
)

(define simplify-division
	(lambda (left right)
		(cond
			((safe-zero? left)
				0)
			((equal? 1 right)
				left)
			((no-variables? left right)
				(/ left right))
			(else
				(list left '/ right))
		)
	)
)

(define simplify-exponent
	(lambda (left right)
		(cond
			((safe-zero? left)
				0)
			((safe-zero? right)
				1)
			((equal? 1 right)
				left)
			((equal? 1 left)
				1)
			((no-variables? left right)
				(expt left right))
			(else
				(list left '** right))
		)
	)
)

(define simplify-func
	(lambda (expr)
		(let ((n (simplify (cadr expr))))
			(if (is-inc? expr)
				(simplify-inc expr n)
				(simplify-dec expr n)
			)
		)
	)
)

(define simplify-inc
	(lambda (expr n)
		(if (number? n)
			(+ n 1)
			expr
		)
	)
)

(define simplify-dec
	(lambda (expr n)
		(if (number? n)
			(- n 1)
			expr
		)
	)
)

(define no-variables? 
	(lambda (left right)
		(and (number? left) (number? right))
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

(define is-op?
	(lambda (expr op)
		(equal? (cadr expr) op)
	)
)

(define is-addition?
	(lambda (expr)
		(is-op? expr '+)
	)
)

(define is-subtraction?
	(lambda (expr)
		(is-op? expr '-)
	)
)

(define is-multiplication?
	(lambda (expr)
		(is-op? expr '*)
	)
)

(define is-exponent?
	(lambda (expr)
		(is-op? expr '**)
	)
)

(define is-division?
	(lambda (expr)
		(is-op? expr '/)
	)
)

(define is-inc?
	(lambda (expr)
		(equal? (car expr) 'inc)
	)
)

(define safe-zero?
	(lambda (n)
		(and (number? n) (zero? n))
	)
)


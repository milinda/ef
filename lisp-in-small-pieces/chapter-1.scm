; Lisp in Small Pieces - Chapter 1 Code
; -------------------------------------

(define eval
  (lambda (e env)
    (if (atom? e)
      (cond
        [(symbol? e) (lookup e env)]
        [(or (number? e) (string? e) (char? e) (boolean? e) (vector? e)) e]
        [else (errorf 'eval "Cannot evaluate" e)])
      (case (car e)
        [(quote) (cadr e)]
        [(if) (if (eval (cadr e) env)
                (eval (caddr e) env)
                (eval (cadddr e) env))]
        [(begin) (eprogn (cdr e) env)]
        [(set!) (update! (cadr e) env (eval (caddr e) env))]
        [(lambda) (make-function (cadr e) (cddr e) env)]
        [else (invoke (eval (car e) env) (evlis (cdr e) env))]))))

(define eprogn
  (lambda (e env)))

(define update!
  (lambda (var env val)))

(define make-function
  (lambda (vars body env)))

(define invoke
  (lambda (fn args env)))

(define lookup
  (lambda (var env)))



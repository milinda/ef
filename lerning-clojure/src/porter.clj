(in-ns 'porter)
(clojure.core/refer 'clojure.core)

(defn pop-stemmer-on
  [predicate stemmer]
  (if (and (seq (:word stemmer)) (predicate stemmer))
    (recur predicate (pop-word stemmer))
    stemmer))

(def vowel-letter? #{\a \e \i \o \u})

(defn consonant?
  "Returns true if the ith character in a stemmer is a consonant. i defaults to :index."
  ([stemmer]
   (consonant? stemmer (get-index stemmer)))
  ([stemmer i]
   (let [c (nth (:word stemmer) i)]
     (cond (vowel-letter? c) false
           (=c \y) (if (zero? i)
                     true
                     (not (consonant? stemmer (dec i))))
           :else true))))

(def vowel? (complement consonant?))

(defn vowel-in-stem?
  "true iff 0...j contains a vowel"
  [stemmer]
  (let [j (get-index stemmer)]
    (loop [i 0]
      (cond (> i j) false
            (consonant? stemmer i) (recur (inc i))
            :else true))))

(defn double-c?
  ([stemmer]
   (double-c? stemmer (get-index stemmer)))
  ([stemmer j]
   (and (>= j 1)
        (= (nth (:word stemmer) j)
           (nth (:word stemmer) (dec j)))
        (consonant? stemmer j))))

(defn cvc?
  ([stemmer]
   (cvc? stemmer (get-index stemmer)))
  ([stemmer i]
   (and (>= i 2)
        (consonant? stemmer (- i 2))
        (vowel? stemmer (dec i))
        (consonant? stemmer i)
        (not (#{\w \x \y} (nth (:word stemmer) i))))))

(defn m
  "Measure the number of consonant sequences between the start
  of word and position j. If c is a consonant sequence and v a vowel
  sequence, and <...> indicates arbitrary presence,
    <c><v>        -> 0
    <c>vc<v>      -> 1
    <c>vcvc<v>    -> 2
    <c>vcvcvc<v>  -> 3
    ...
  "
  [stemmer]
  (let [
        j (get-index stemmer)
        count-v (fn [n i]
                  (cond (> i j) [:return n i]
                        (vowel? stemmer i) [:break n i]
                        :else (recur n (inc i))))
        count-c (fn [n i]
                  (cond (> i j) [:return n i]
                        (consonant? stemmer i) [:break n i]
                        :else (recur n (inc i))))
        count-cluster (fn [n i]
                        (let [[stage1 n1 i1] (count-c n i)]
                          (if (= stage1 :return)
                            n1
                            (let [[stage2 n2 i2] (count-v (inc n1) (inc i1))]
                              (if (= stage2 :return)
                                n2
                                (recur n2 (inc i2)))))))
        [stage n i] (count-v 0 0)]
    (if (= stage :return)
      n
      (count-cluster n (inc i)))))

(defn ends?
  "true if the word ends with s."
  [stemmer s]
  (let [word (subword stemmer), sv (vec s), j (- (count word) (count sv))]
    (if (and (pos? j) (= (subvec word j) sv))
      [(assoc stemmer :index (dec j)) true]
      [stemmer false])))

(defn set-to
  "this sets the last j+1 characters to x and readjusts the length of b."
  [setmmer new-end]
  (reset-index (into (subword stemmer) new-end)))



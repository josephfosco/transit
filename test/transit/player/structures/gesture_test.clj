;    Copyright (C) 2017  Joseph Fosco. All Rights Reserved
;
;    This program is free software: you can redistribute it and/or modify
;    it under the terms of the GNU General Public License as published by
;    the Free Software Foundation, either version 3 of the License, or
;    (at your option) any later version.
;
;    This program is distributed in the hope that it will be useful,
;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;    GNU General Public License for more details.
;
;    You should have received a copy of the GNU General Public License
;    along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns transit.player.structures.gesture-test
  (:require
   [transit.test-utils :use [with-private-fns]]
   [transit.melody.dur-info :use [create-dur-info]]
   )
  (:use clojure.test
        transit.player.structures.gesture
        )
  )

(defn variables-fixture
  [f]
  (def base-note 60)
  (f)
  )

(use-fixtures :once variables-fixture)

(with-private-fns [transit.player.structures.gesture [GESTURE-MAX-SKIP]]
  (deftest test-check-validate-possible-gesture
    (testing "returns nil if too large jump at end of gesture"
      (let [test-gesture [{:note base-note
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (+ base-note 2)
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (+ base-note (var-get GESTURE-MAX-SKIP) 3)
                           :dur-info (create-dur-info :dur-millis 100)}
                          ]
            ]
        (is (= nil (validate-possible-gesture test-gesture))
            ))
      )

    (testing "returns nil if too large jump at start of gesture"
      (let [test-gesture [{:note base-note
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (+ base-note (+ (var-get GESTURE-MAX-SKIP) 1))
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (+ base-note (+ (var-get GESTURE-MAX-SKIP) 2))
                           :dur-info (create-dur-info :dur-millis 100)}
                          ]
            ]
        (is (= nil (validate-possible-gesture test-gesture))
            ))
      )

    (testing "returns nil if too large jump in middle of gesture"
      (let [test-gesture [{:note base-note
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (var-get GESTURE-MAX-SKIP) 1)
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (var-get GESTURE-MAX-SKIP) 2)
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (var-get GESTURE-MAX-SKIP) 3)
                           :dur-info (create-dur-info :dur-millis 100)}
                          ]
            ]
        (is (= nil (validate-possible-gesture test-gesture))
            ))
      )

    (testing "returns gesture if valid gesture"
      (let [test-gesture [{:note base-note
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (dec (var-get GESTURE-MAX-SKIP)))
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (- (var-get GESTURE-MAX-SKIP) 2))
                           :dur-info (create-dur-info :dur-millis 100)}
                          {:note (- base-note (- (var-get GESTURE-MAX-SKIP) 3))
                           :dur-info (create-dur-info :dur-millis 100)}
                          ]
            ]
        (is (= test-gesture (validate-possible-gesture test-gesture))
            ))
      )

    )
  )

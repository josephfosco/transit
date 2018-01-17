;    Copyright (C) 2018  Joseph Fosco. All Rights Reserved
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

(ns transit.ensemble.ensemble-status-test
  (:use clojure.test
        transit.ensemble.ensemble-status
        )
  )

(deftest test-get-note-dur-list
  (testing "adds all within range correctly"
    (let [note-times ['(1000000001900 100)
                      '(1000000001000 700)
                      '(1000000000454 20)
                      '(1000000000450 400)
                      '(1000000000300 350)
                      '(1000000000010 50)
                      '(1000000000000 100)
                      ]
          ]
      (is (= (get-note-dur-list note-times 1000000000000 1000000002000)
             '(100 700 20 400 350 50 100)))
      )
    )

  (testing "adds early start times correctly"
    (let [note-times ['(1100000001900 100)
                      '(1100000001000 700)
                      '(1100000000454 20)
                      '(1100000000450 400)
                      '(1100000000300 350)
                      '(1100000000010 50)
                      '(1100000000000 100)
                      '(1099999999950 200)
                      '(1099999999900 100)
                      ]
          ]
      (is (= (get-note-dur-list note-times 1100000000000 1100000002000)
             '(100 700 20 400 350 50 100 150 0)))
      )
    )

  (testing "adds late durations correctly"
    (let [note-times ['(1000000002000 100)
                      '(1000000001950 100)
                      '(1000000001900 100)
                      '(1000000001000 700)
                      '(1000000000454 20)
                      '(1000000000450 400)
                      '(1000000000300 350)
                      '(1000000000010 50)
                      '(1000000000000 100)
                      ]
          ]
      (is (= (get-note-dur-list note-times 1000000000000 1000000002000)
             '(0 50 100 700 20 400 350 50 100)))
      )
    )
  )

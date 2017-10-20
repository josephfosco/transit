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

(ns transit.player.player-methods-test
  (:require
   [transit.player.player :use [create-player]]
   [transit.player.structures.motif :use [create-motif]]
   [transit.player.structures.random-event :use [create-random-event]]
   )
  (:import
   [transit.player.structures.motif Motif]
   [transit.player.structures.random_event RandomEvent]
   )
  (:use clojure.test
        transit.player.player-methods
        )
  )

(deftest test-remove-methods
  (testing "removes methods from player"
    (let [method1 (create-method-info select-instrument-for-player 10)
          method2 (create-method-info select-key 1)
          method3 (create-method-info select-scale 1)
          method4 (create-method-info select-instrument-for-player 1)
          method5 (create-method-info play-random 1)
          ]
        (is (=
             {:id 1
              :methods [method2 method3 method5]
              }
             (remove-methods {:id 1
                              :methods [method1
                                        method2
                                        method3
                                        method4
                                        method5
                                        ]}
                             select-instrument-for-player
                             select-mm
                             )
             )
            ))
    )
  )

(deftest test-add-methods
  (testing "adds methods to player"
    (let [method1 (create-method-info play-random 1)
          method2 (create-method-info select-key 1)
          method3 (create-method-info select-scale 1)
          orig-methods [method1 method2 method3]
          updated-methods (add-methods orig-methods
                                       select-instrument-for-player 2
                                       select-mm 2
                                       select-scale 3
                                       )
          ]
      (is (= 6 (count updated-methods)))
      (is (= orig-methods (take 3 updated-methods)))
      (is (= (for [mthd updated-methods] (:method mthd))
             (list play-random
              select-key
              select-scale
              select-instrument-for-player
              select-mm
              select-scale))
          )
      (is (= (for [mthd updated-methods] (:weight mthd)) '(1 1 1 2 2 3))
          )
      ))
  )

(deftest test-remove-structure-type
  (testing "removes all structures of a type from player"
    (let [struct1 (create-random-event)
          struct2 (create-motif)
          struct3 (create-random-event)
          struct4 (create-motif)
          player (assoc (create-player :id 1)
                        :structures [struct1 struct2 struct3 struct4]
                        )]
        (is (= (assoc player :structures [struct1 struct3])
             (remove-structure-type player Motif)
             )
            ))
    )

  (testing "removes all structures in a list from player"
    (let [struct1 (create-random-event)
          struct2 (create-motif)
          struct3 (create-random-event)
          struct4 (create-motif)
          player (assoc (create-player :id 1)
                        :structures [struct1 struct2 struct3 struct4]
                        )]
        (is (= (assoc player :structures [])
               (remove-structure-type player '(RandomEvent Motif))
             )
            ))
    )
  )

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

(ns transit.player.player-test
  (:require
   [transit.player.player :use [create-player]]
   [transit.player.structures.motif :use [create-motif]]
   [transit.player.structures.random-event :use [create-random-event]]
   )
  (:import
   [transit.player.structures.motif Motif]
   )
  (:use clojure.test
        transit.player.player-methods
        )
  )

(deftest test-remove-structure-type
  (testing "removes all structures af a type from player"
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
  )

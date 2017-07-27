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

(ns transit.ensemble.player-play-note-test
  (:use clojure.test
        transit.ensemble.player-play-note
        )
  (:require
   [transit.ensemble.player-methods :refer [CONTINUE NEXT-METHOD]]
   )
  )

(deftest test-stop-running-methods?
  (testing "returns false if there are mathods and status no NEXT-METHOD"
    (is (=
         true
         (stop-running-methods? (+ (System/currentTimeMillis) 1000)
                                [{}
                                 {:methods [1 1]}
                                 0
                                 {:status CONTINUE}
                                 ])
         )
        )
    )
  (testing "returns true if there are no methods in the player"
    (is (=
         true
         (stop-running-methods? (+ (System/currentTimeMillis) 1000)
                                [{}
                                 {:methods {}}
                                 0
                                 {:status CONTINUE}
                                 ])
         )
        )
    )
  (testing "returns true if status is NEXT-METHOD"
    (is (=
         true
         (stop-running-methods? (+ (System/currentTimeMillis) 1000)
                                [{}
                                 {:methods [1 1]}
                                 0
                                 {:status NEXT-METHOD}
                                 ])
         )
        )
    )

  )

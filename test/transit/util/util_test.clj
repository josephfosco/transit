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

(ns transit.util.util-test
  (:use clojure.test
        transit.util.util
        )
  )

(deftest test-remove-element-from-vector
  (testing "removes inner element from vector"
    (is (=
         (remove-element-from-vector [["a" 0] ["b" 1] ["c" 2] ["d" 3] ["e" 4]] 2)
         [["a" 0] ["b" 1] ["d" 3] ["e" 4]]
         )
        )
    )

  (testing "removes first element from vector"
    (is (=
         (remove-element-from-vector [["a" 0] ["b" 1] ["c" 2] ["d" 3] ["e" 4]] 0)
         [["b" 1] ["c" 2] ["d" 3] ["e" 4]]
         )
        )
    )

    (testing "removes last element from vector"
    (is (=
         (remove-element-from-vector [["a" 0] ["b" 1] ["c" 2] ["d" 3] ["e" 4]] 4)
         [["a" 0] ["b" 1] ["c" 2] ["d" 3]]
         )
        )
    )
)

;    Copyright (C) 2017-2018  Joseph Fosco. All Rights Reserved
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

(ns transit.util.util
  (:require
   [clojure.core.async :refer [<! chan close! go-loop pub]]
   [transit.config.config :refer [get-setting]]
   )
  )

(def msgs-in-channel (atom nil))
(def msgs-pub (atom nil))

(defn remove-element-from-vector
  "Returns a vector with element at ndx removed
   All elements after ndx are moved forward by 1 (e.g
   element n becomes element n - 1 etc....)
   Throws an IndexOutOfBoundsException if ndx > (count vctr)
  "
  [vctr ndx]
  (into (subvec vctr 0 ndx) (subvec vctr (inc ndx)))
  )

(defn get-msg-channel
  []
  @msgs-in-channel
  )

(defn get-msg-pub
  []
  @msgs-pub
  )

(defn drain-chan
  "Used to remove any remaining messages from a closed channel"
  [ch]
  (go-loop []
    (if (some? (<! ch))
      (recur))
      )
  )

(defn start-msg-channel
  []
  (reset! msgs-in-channel (chan (* 2 (get-setting :num-players))))
  (reset! msgs-pub (pub @msgs-in-channel :msg))
  )

(defn close-msg-channel
  []
  (close! @msgs-in-channel)
  (reset! msgs-in-channel nil)
  )

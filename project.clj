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

(defproject transit "0.1.0-SNAPSHOT"
  :description "TRANSMIT: an improvisational music system"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License version 3"
            :url "http://www.gnu.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.465"]
                 [overtone "0.10.3"]
                 ]
  :jvm-opts ^:replace [] ;; turns off JVM arg TieredStopAtLevel=1
  :main ^:skip-aot transit.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  )

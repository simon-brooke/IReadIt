(ns ireadit.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [ireadit.core-test]))

(doo-tests 'ireadit.core-test)


from __future__ import (absolute_import, division, print_function)
__metaclass__ = type


def pick(k, d):
  return d.get(k, k)


class FilterModule(object):
  def filters(self):
    return {
      'pick': pick,
    }

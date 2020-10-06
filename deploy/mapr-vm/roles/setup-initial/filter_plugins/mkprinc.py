from __future__ import (absolute_import, division, print_function)
__metaclass__ = type


def mkprinc(host, principal):
  return principal.replace('_HOST', host)


class FilterModule(object):
  def filters(self):
    return {
      'mkprinc': mkprinc,
    }

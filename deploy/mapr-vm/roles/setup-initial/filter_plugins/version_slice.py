from __future__ import (absolute_import, division, print_function)
__metaclass__ = type


def version_slice(version_string, num_slices=None, needle='.', return_with_needle=True):
  if return_with_needle:
    return needle.join(version_string.split(needle)[:num_slices])
  else:
    return ''.join(version_string.split(needle)[:num_slices])


class FilterModule(object):
  def filters(self):
    return {
      'version_slice': version_slice,
    }

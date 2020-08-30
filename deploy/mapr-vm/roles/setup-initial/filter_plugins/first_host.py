from __future__ import (absolute_import, division, print_function)
__metaclass__ = type


def first_host(items, default='localhost'):
  # Transform ([], [node1, node2], [node3]) to [node1, node3]
  if items and isinstance(items[0], (list, tuple)):
    items = [item[0] for item in items if item]

  return items[0] if items else default


class FilterModule(object):
  def filters(self):
    return {
      'first_host': first_host,
    }

#!/bin/bash

for i in {30..0}; do
    if curl elasticsearch:9200; then
        curl -H "Content-Type: application/json" -XPUT elasticsearch:9200/_template/olx -d '
            {
                "index_patterns": [
                    "olx*"
                ],
                "settings": {
                    "index.number_of_shards": 1
                },
                "mappings": {
                    "cars": {
                        "properties": {
                            "engine_cubic_cm": {
                                "type": "double"
                            },
                            "link": {
                                "type": "text"
                            },
                            "message": {
                                "type": "text",
                                "fielddata": true
                            },
                            "model": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "price_usd": {
                                "type": "double"
                            },
                            "published": {
                                "type": "date"
                            },
                            "race_km": {
                                "type": "double"
                            },
                            "tags": {
                                "type": "text"
                            },
                            "title": {
                                "type": "text",
                                "fielddata": true
                            },
                            "year": {
                                "type": "double"
                            }
                        }
                    }
                }
            }';
            break;
    fi
    sleep 2
done

curl -H "Content-Type: application/json" -XPUT "http://elasticsearch:9200/_cluster/settings" -d'
{
  "persistent": {
    "cluster": {
      "routing": {
        "allocation.disk.threshold_enabled": false
      }
    }
  }
}'

curl -H "Content-Type: application/json" -XPUT "http://elasticsearch:9200/olxua/_settings" -d'{
    "index": {
    "blocks": {
    "read_only_allow_delete": "false"
    }
    }
    }'

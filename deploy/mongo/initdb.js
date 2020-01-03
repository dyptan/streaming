db = db.getSiblingDB('olx')
db.createCollection( "cars", { capped: true, size: 1024 } )
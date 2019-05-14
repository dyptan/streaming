function applySavedFilter(id) {
            $.ajax({
                url: "http://localhost:8081/user/[[${userName}]]/filter/"+id
            }).then(function(data) {
                $("#models").val(data.models)
                $("#year_from").val(data.year_from)
                $("#year_to").val(data.year_to)
                $("#price_from").val(data.price_from)
                $("#price_to").val(data.price_to)
                $("input:radio").not(this).attr("checked", false)
                $(this).attr('checked', true)
                $("#brands").val(data.brands)
         })
        }

        $(document).ready(function() {
            $.ajax({
                url: "http://localhost:8081/user/[[${userName}]]/filters"
            }).then(function(data) {
                $.each(data, function(i, filter) {
                    $('<input>').attr({
                        type: 'radio',
                        id: i,
                        onchange: 'applySavedFilter(this.id)'
                    }).appendTo('.filters').after("Brands: "+filter.brands+", Models: "+filter.models
                    +", year from: "+filter.year_from+", year to: "+filter.year_to+
                    ", price from: "+filter.price_from+", price to: "+filter.price_to+"<br>");
                });
            });
        });
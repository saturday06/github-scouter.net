declare var $, GithubScouter, google
google.load("visualization", "1", {packages:["corechart"]})

$(() => {
    function showChart(userName, powerLevel) {
        var data = google.visualization.arrayToDataTable([
            ['Task', 'Hours per Day'],
            ['攻撃力', powerLevel.atk],
            ['知力', powerLevel.int],
            ['素早さ', powerLevel.agi]
        ])
        var width = $("#power_level_message").width() // this?
        var options = {
            sliceVisibilityThreshold: 0,
            animation:{
                duration: 1000,
                easing: 'out'
            },
            width: width
        }
        $('#power_level_chart').width(width)

        // What!?
        var chart = new google.visualization.PieChart(document.getElementById('power_level_chart'))
        chart.draw(data, options)
    }

    function showResult(userName, powerLevel) {
        var href = "https://github.com/" + userName
        var message = '<a class="user-name" href="' + href + '">' + userName +
            '</a>さんのGitHub戦闘力は<br><span class="total-power-level">' +
            "　" + Math.round(powerLevel.total() * 10) / 10 + "</span>です。";
        $("#power_level_message").html(message).show(300, () => { showChart(userName, powerLevel) })
    }

    function measure() {
        $("#power_level_message").hide(200).empty()
        $("#power_level_chart").empty()
        $(".error-message").hide()
        $(".measure-spinner").show()
        var scouter = new GithubScouter()
        var userName = $("#github_id").val()

        scouter.measure(userName, (powerLevel) => {
            console.log(powerLevel)
            $(".measure-spinner").hide()
            showResult(userName, powerLevel)
        }, (e) => {
            console.log(e)
            $(".measure-spinner").hide()
            $(".error-message").text(e.toString()).show()
        })

        return false
    }

    $("#measure").click(measure)
    $("#github_id_form").submit(measure)
})

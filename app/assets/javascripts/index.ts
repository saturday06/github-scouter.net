declare var $, google, GithubScouter, CoreAnimation, moment
google.load("visualization", "1", {packages:["corechart"]})

$(() => {
    function showChart(userName, powerLevel) {
        var data = google.visualization.arrayToDataTable([
            ['Parameter', 'Value'],
            ['攻撃力', powerLevel.atk],
            ['知力', powerLevel.int],
            ['素早さ', powerLevel.agi]
        ])
        var width = $("#power_level_message").width() // this?
        var options = {
            sliceVisibilityThreshold: 0,
            width: width
        }
        $('#power_level_chart').width(width)

        var chart = new google.visualization.PieChart(document.getElementById('power_level_chart'))
        chart.draw(data, options)
    }

    function showResult(userName, powerLevel) {
        var href = "https://github.com/" + userName
        var message = '<a class="user-name" href="' + href + '">' + userName +
            '</a>さんのGitHub戦闘力は<br><span class="total-power-level">' +
            "　" + Math.round(powerLevel.total() * 10) / 10 + "</span>です。";
        $("#power_level_message").html(message)
        if (powerLevel.cached) {
            $("#cache_message").text("※ " + powerLevel.timestamp.format() + "のキャッシュです")
        }

        showChart(userName, powerLevel)

        var animation = new CoreAnimation()
        animation.duration = 300;
        animation.keyframes = [
            {opacity: 0},
            {opacity: 1}
        ];
        animation.target = document.getElementById("power_level")
        animation.play()
    }

    function measure() {
        $("#power_level").css("opacity", 0).hide()
        $(".error-message").hide()
        $(".measure-spinner").show()
        var scouter = new GithubScouter()
        var userName = $("#github_id").val()

        scouter.measure(userName, (powerLevel) => {
            console.log(powerLevel)
            $(".measure-spinner").hide()
            $("#power_level").show()
            showResult(userName, powerLevel)

            // キャッシュにためときたいため、いちおう叩く
            // userNameはvalidation済みのはず
            // TODO: サーバー側でキューイング
            $.get('powerLevels/' + userName, (response) => {
                // console.log(response)
            })
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

function enableIntro(option) {

    startIntro = function () {
        var intro = introJs();
//        a=[{"intro":"here you can see your friends","position":"down","element":"friendsLI84"},{"intro":"here you can see your previous conversations and new messages","position":"left","element":"threadLI85"}];
        a = option;
        intro.setOptions({
            steps: a
        });
        newJob(intro);
        if (typeof(noIntro) == "undefined" || noIntro == true) {
            nextJob();
        }

    }

    function newJob(intro) {
        if (typeof(introQueue) == "undefined") {
            introQueue = [];
            noIntro = true;
        }
        introQueue.push(intro);
    }

    function nextJob() {
        nextJobObj = introQueue.shift();
        if (typeof(nextJobObj) == "undefined") {
            noIntro = true;
            return;
        }
        noIntro = false;
        setTimeout(function () {
            nextJobObj.oncomplete(function () {
                nextJob();
            });
            nextJobObj.onexit(function () {
                nextJob();
            });
            nextJobObj.start();
        }, 1000);

    }
}

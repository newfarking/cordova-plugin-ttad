var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'CordovaTTADPlugin', 'coolMethod', [arg0]);
};


exports.showAdBanner = function (arg0, success, error) {
    exec(success, error, 'CordovaTTADPlugin', 'showAdBanner', [arg0]);
};


exports.showInteractionAd = function (arg0, success, error) {
    exec(success, error, 'CordovaTTADPlugin', 'showInteractionAd', [arg0]);
};


exports.showRewardAd = function (arg0, success, error) {
    exec(success, error, 'CordovaTTADPlugin', 'showRewardAd', [arg0]);
};
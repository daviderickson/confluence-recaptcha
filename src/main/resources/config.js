AJS.toInit(function() {
    function populateForm() {
        AJS.$.ajax({
            url: baseUrl + "/rest/recaptcha/1.0/",
            dataType: "json",
            success: function(config) {
                AJS.$("#publicKey").attr("value", config.publicKey);
                AJS.$("#privateKey").attr("value", config.privateKey);
            }
        });
    }

    function updateConfig() {
        AJS.$.ajax({
            url: baseUrl + "/rest/recaptcha/1.0/",
            type: "PUT",
            contentType: "application/json",
            data: '{ "publicKey": "' + AJS.$("#publicKey").attr("value") + '", "privateKey": "' +  AJS.$("#privateKey").attr("value") + '" }',
            processData: false
        });
    }     
    populateForm();

    AJS.$("#admin").submit(function(e) {
        e.preventDefault();
        updateConfig();
    });
});
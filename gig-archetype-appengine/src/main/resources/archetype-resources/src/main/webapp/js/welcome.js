$(function() {
	$('#welcome').hide();
	$('#message').hide();
	$('#shell').hide();
	$('#cursor').hide();
	setTimeout(function() {
		$('#welcome').show();
		setTimeout(function() {
			wait(0, 3, function() {
				$.ajax({
					url: 'WelcomeEndpoint/get', 
					success: function(welcome) {
						$('#status').append(' [ <span class="success">' + welcome.status + '</span> ]');
						message();
					}, 
					error: function() {
						$('#status').append(' [ <span class="error">NOT READY</span> ]');
						message();
					}
				});
			});
		}, 1000);
	}, 1000);
});

function wait(i, times, callback) {
	if (i < times) {
		$('#status').append('.');
		setTimeout(function() {
			wait(i + 1, times, callback);
		}, 1000);
	} else {
		callback();
	}
}

function message() {
	setTimeout(function() {
		$('#message').show();
		setTimeout(function() {
			$('#shell').show();
			setTimeout(function() {
				blink(true);
			}, 1000);
		}, 1000);
	}, 1000);
}

function blink(show) {
	setTimeout(function() {
		if (show) {
			$('#cursor').show();
		} else {
			$('#cursor').hide();
		}
		blink(!show);
	}, 1000);
}

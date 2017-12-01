$(function() {
	'use strict';

	var client;
	var currentValue = 0;

	function showMessage(msg) {
		// Set the Match values
		if ($('#match').val() == "")
			$('#match').val(msg.matchId);
		if ($('#opponent').val() == "" && msg.opponentName != ""
				&& msg.opponentName != $('#from').val())
			$('#opponent').val(msg.opponentName);
		else if ($('#opponent').val() == "" && msg.opponentName != ""
				&& msg.opponentName == $('#from').val())
			$('#opponent').val(msg.destination);

		// Just handle the buttons to play
		if ((msg.status == "Start" || msg.status == "Reconnected") && $('#from').val() != "Computer")
			$('#send1,#send2,#send3').prop('disabled', false);
		else if (msg.status == "Disconnect")
			$('#send1,#send2,#send3').prop('disabled', true);

		// Handle the message content
		if (msg.status == "Alert" && msg.destination == $('#from').val())
			alert('Alert', msg.message);
		else if (msg.status == "Alert" && msg.destination != $('#from').val())
			return;
		else if (msg.status == "Error")
			errorAlert('Error!', msg.message);
		else if (msg.status == "Finished") {
			successAlert('Match Finished!', msg.message);
			disconnect();
		} else {
			$('#messages').append(
					'<tr>' + '<td>' + msg.matchId + '</td>' + '<td>'
							+ msg.message + '</td>' + '<td>'
							+ new Date(msg.time).toLocaleString() + '</td>'
							+ '</tr>');
		}
		if ($('#from').val() == "Computer")
			$('#send1,#send2,#send3').css('disabled', true);
	}

	function setConnected(connected) {
		$("#connect").prop("disabled", connected);
		$("#disconnect").prop("disabled", !connected);
		$('#from').prop('disabled', connected);

		if (connected) {
			$("#conversation").show();
			$('#opponent').val("");
		} else
			$("#conversation").hide();
		$("#messages").html("");
	}

	function sendMessage(option) {
		var match = $('#match').val();
		if (match == "")
			match = "NEWMATCH"
		client.send("/app/game/" + match, {}, JSON.stringify({
			from : $("#from").val(),
			currentValue : (match == "NEWMATCH" ? "" : currentValue),
			option : (match == "NEWMATCH" ? "" : option),
		}));
	}

	$("form").on('submit', function(e) {
		e.preventDefault();
	});

	$('#from').on('blur change keyup', function(ev) {
		$('#connect').prop('disabled', $(this).val().length == 0);
	});
	$('#connect,#disconnect,#send1,#send2,#send3').prop('disabled', true);

	$('#connect').click(function() {
		client = Stomp.over(new SockJS('/game'));
		client.connect({}, function(frame) {
			setConnected(true);
			client.subscribe('/match/messages', function(message) {
				showMessage(JSON.parse(message.body));
			});
			sendMessage("");
		});
	});
	
	$('#computer').click(function() {
		$('#from').val("Computer");
		$('#connect').click();
		$('#send1,#send2,#send3').css('disabled', true);
	});

	function disconnect() {
		if (client != null) {
			client.disconnect();
			setConnected(false);
		}
		client = null;
		$('#match').val("");
	}

	$('#disconnect').click(function() {
		sendMessage("9");
		disconnect();
	});

	$('#send1').click(function() {
		sendMessage("1");
	});

	$('#send2').click(function() {
		sendMessage("2");
	});

	$('#send3').click(function() {
		sendMessage("3");
	});
});

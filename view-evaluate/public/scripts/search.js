$(document).ready(function() {
	$("#search_form").submit(function(event) {
		var query = $('#q').val();
		var title_types = new Array();
		$.each($("input[name='title[]']:checked"), function() {
			title_types.push($(this).val());
		});
		var content_types = new Array();
		$.each($("input[name='content[]']:checked"), function() {
			content_types.push($(this).val());
		});

		$('#results').html('Loading, please wait...');
		$.getJSON('api/clustering_search.json', {
				q: query,
				title_types: title_types.join(';'),
				content_types: content_types.join(';'),
				algorithm: $('#algorithm').val()
			}, function(data) {
			updateResults(data);
		});

		event.preventDefault();
	});
});

function updateResults(data) {
	$('#results').html('');

	for (var i = 0; i < data.length; i++) {
		var result_e = $('<div>')
			.attr('class', 'result')
			.appendTo('#results');
		$('<h2>')
			.html(data[i]['label'])
			.appendTo(result_e);
		$('<small>')
			.html("Score: " + data[i]['score'])
			.appendTo(result_e);

		var documents = data[i]['documents'];
		for (var n = 0; n < documents.length; n++) {
			$('<p>')
				.html("<a href=\"" + documents[n]['_source']['url'] + "\">" + documents[n]['_source']['title'] + "</a>")
				.appendTo(result_e);
		}
	}
}
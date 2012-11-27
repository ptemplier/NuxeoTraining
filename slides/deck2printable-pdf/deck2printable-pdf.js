/*
 * Converts deck.js to pdf using phantomjs.
 * Format used is adapted to training printing format
 */

var webpage = require('webpage'),
		page = webpage.create(),
    system = require('system'),
    url = system.args[1] || 'index.html',
    fs = require('fs'),
    imageSources = [],
    imageTags;

page.onLoadFinished = function(status) {
	var slideCount;

	if (status !== 'success') {
		console.log('Target file not found.');
		phantom.exit();
	}

	page.viewportSize = {
			width: 1654,
			height: 2339
	};

	slideCount = page.evaluate(function() {
		var $ = window.jQuery;

		$('html').removeClass('csstransitions cssreflections');
		$('html, body').css({
			'width': 1580,
			'height': 2200,
			'overflow': 'hidden'
		});
		$.deck('.slide');
		return $.deck('getSlides').length;
	});

	var src = system.args[2] || 'output.pdf';
	imageSources.push(src);
	console.log('Rendering slide');
	page.render(src);

	phantom.exit();
};

page.open(url);

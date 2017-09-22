$(document).ready(function(){
	$("#v-pills-tab a").on("shown.bs.tab", function(event) {
		//Fix previous pill stuff
		prevPill = event.relatedTarget.getAttribute("href");;
		$(prevPill + " .nav a").removeClass("active");
		$(prevPill + " .tab-content .tab-pane").removeClass("active show");

		//Fix current pill stuff
		var currPill = event.target.getAttribute("href");

		$(currPill + " .nav a:first").tab("show");
	});
});
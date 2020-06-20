// Change app-header background color on scroll
$(function () {
    $(document).scroll(function () {
        var $nav = $(".navbar-container");
        $nav.toggleClass('scrolled', $(this).scrollTop() > $nav.height());
    });
});
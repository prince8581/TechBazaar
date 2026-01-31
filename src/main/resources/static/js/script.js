const menuBtn = document.getElementById('menuBtn');
const techMenu = document.getElementById('techMenu');

menuBtn.addEventListener('click', () => {
    const rect = menuBtn.getBoundingClientRect();
    techMenu.style.top = rect.bottom + 'px'; // niche khule
    techMenu.style.left = rect.left + 'px';  // left align
    const bsCollapse = new bootstrap.Collapse(techMenu, {
        toggle: true
    });
});


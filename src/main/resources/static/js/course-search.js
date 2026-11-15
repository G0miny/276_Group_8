
const courseForm = document.getElementById("courseForm");
const courseResults = document.getElementById("courseResults");
const searchStatus = document.getElementById("searchStatus");

courseForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const year = document.getElementById("year").value;
    const term = document.getElementById("term").value;
    const department = document.getElementById("department").value.trim();
    const course = document.getElementById("course").value.trim();

    const params = new URLSearchParams({
        year: year,
        term: term,
        department: department.toLowerCase(),
        course: course
    });

    courseResults.replaceChildren();
    searchStatus.textContent = "Searching...";

    try {
        const response = await fetch("/api/courses?" + params);

        if (!response.ok) {
            throw new Error("Could not find courses");
        }

        const sections = await response.json();

        searchStatus.textContent = sections.length + " sections found";

        for (const section of sections) {

            const card = document.createElement("div");
            card.className = "course-card";

            const title = document.createElement("h3");
            title.textContent =
                department.toUpperCase() + " " +
                course + " - " + section.title;

            const info = document.createElement("p");
            info.textContent =
                "Section: " + section.text +
                " | Type: " + section.sectionCode;

            card.append(title, info);
            courseResults.appendChild(card);
        }

    } catch (error) {
        searchStatus.textContent = error.message;
    }
});

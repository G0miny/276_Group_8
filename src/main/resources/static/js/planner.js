
const courseForm = document.getElementById("courseForm");
const courseResults = document.getElementById("courseResults");
const searchStatus = document.getElementById("searchStatus");

const savedCourses = document.getElementById("savedCourses");
const savedStatus = document.getElementById("savedStatus");
const weeklyTimetable = document.getElementById("weeklyTimetable");


// Search for courses
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

            // View schedule button
            if (section.classType === "e") {

                const viewButton = document.createElement("button");
                viewButton.textContent = "View Schedule";

                const scheduleInfo = document.createElement("div");
                scheduleInfo.hidden = true;

                viewButton.addEventListener("click", async function() {

                    scheduleInfo.hidden = false;
                    scheduleInfo.textContent = "Loading schedule...";

                    const detailParams = new URLSearchParams(params);
                    detailParams.set("section", section.value);

                    try {
                        const response = await fetch(
                            "/api/courses/details?" + detailParams
                        );

                        if (!response.ok) {
                            throw new Error("Could not load schedule");
                        }

                        const detail = await response.json();
                        const meetings = detail.courseSchedule || [];

                        scheduleInfo.replaceChildren();

                        if (meetings.length === 0) {
                            scheduleInfo.textContent = "No schedule available";
                        }

                        for (const meeting of meetings) {
                            const p = document.createElement("p");

                            p.textContent =
                                meeting.days + " " +
                                meeting.startTime + " - " +
                                meeting.endTime + " (" +
                                meeting.campus + ")";

                            scheduleInfo.appendChild(p);
                        }

                    } catch (error) {
                        scheduleInfo.textContent = error.message;
                    }
                });

                card.append(viewButton, scheduleInfo);
            }

            // Add to timetable button
            const addButton = document.createElement("button");
            addButton.textContent = "Add to Timetable";

            const message = document.createElement("p");

            addButton.addEventListener("click", async function() {

                addButton.disabled = true;
                message.textContent = "Adding...";

                const selection = {
                    year: Number(year),
                    term: term,
                    department: department.toLowerCase(),
                    course: course,
                    section: section.value
                };

                try {
                    const response = await fetch("/api/my-timetable", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(selection)
                    });

                    if (response.status === 409) {
                        addButton.textContent = "Already Added";
                        message.textContent = "Section already selected";
                        return;
                    }

                    if (response.status === 401) {
                        throw new Error("Please log in first");
                    }

                    if (!response.ok) {
                        throw new Error("Could not add section");
                    }

                    addButton.textContent = "Added";
                    message.textContent = "Section added successfully";

                    await loadSavedCourses();

                } catch (error) {
                    message.textContent = error.message;
                    addButton.disabled = false;
                }
            });

            card.append(addButton, message);
            courseResults.appendChild(card);
        }

    } catch (error) {
        searchStatus.textContent = error.message;
    }
});


// Load saved courses from database
async function loadSavedCourses() {

    savedCourses.replaceChildren();
    savedStatus.textContent = "Loading saved courses...";

    try {
        const response = await fetch("/api/my-timetable");

        if (!response.ok) {
            throw new Error("Could not load saved courses");
        }

        const selections = await response.json();

        savedStatus.textContent = selections.length + " saved sections";

        for (const selection of selections) {

            const card = document.createElement("div");
            card.className = "course-card";

            const title = document.createElement("h3");
            title.textContent =
                selection.department.toUpperCase() + " " +
                selection.course + " - " +
                selection.title;

            const info = document.createElement("p");
            info.textContent = "Section: " + selection.section;

            card.append(title, info);

            // Display class times
            for (const meeting of selection.meetings) {

                const p = document.createElement("p");

                p.textContent =
                    meeting.day + " " +
                    meeting.startTime + " - " +
                    meeting.endTime;

                card.appendChild(p);
            }

            // Remove button
            const removeButton = document.createElement("button");
            removeButton.textContent = "Remove";

            removeButton.addEventListener("click", async function() {

                removeButton.disabled = true;

                try {
                    const response = await fetch(
                        "/api/my-timetable/" + selection.id,
                        { method: "DELETE" }
                    );

                    if (!response.ok) {
                        throw new Error("Could not remove section");
                    }

                    await loadSavedCourses();

                } catch (error) {
                    savedStatus.textContent = error.message;
                    removeButton.disabled = false;
                }
            });

            card.appendChild(removeButton);
            savedCourses.appendChild(card);
        }

        // Update the weekly timetable
        showWeeklyTimetable(selections);

    } catch (error) {
        savedStatus.textContent = error.message;
        weeklyTimetable.replaceChildren();
    }
}


// Display classes by day
function showWeeklyTimetable(selections) {

    weeklyTimetable.replaceChildren();

    const days = ["Mo", "Tu", "We", "Th", "Fr"];

    for (const day of days) {

        const row = document.createElement("tr");

        const dayCell = document.createElement("td");
        dayCell.textContent = day;

        const classCell = document.createElement("td");

        const classes = [];

        // Find all classes on this day
        for (const selection of selections) {

            for (const meeting of selection.meetings) {

                if (meeting.day === day) {

                    classes.push({
                        name: selection.department.toUpperCase() +
                            " " + selection.course +
                            " (" + selection.section + ")",
                        startTime: meeting.startTime,
                        endTime: meeting.endTime
                    });
                }
            }
        }

        // Sort classes by starting time
        classes.sort((a, b) =>
            a.startTime.localeCompare(b.startTime)
        );

        for (const item of classes) {

            const p = document.createElement("p");

            p.textContent =
                item.name + " " +
                item.startTime + " - " +
                item.endTime;

            classCell.appendChild(p);
        }

        if (classes.length === 0) {
            classCell.textContent = "No classes";
        }

        row.append(dayCell, classCell);
        weeklyTimetable.appendChild(row);
    }
}


// Load the student's timetable when the page opens
loadSavedCourses();

"use strict"


// https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
/**
 * Make async POST Requests
 * @param {string} url the url you want to request
 * @param {json} data _OPTIONAL_ - data you want to fetch in json format
 * <br><br>
 * _mode: cors_ <br>
 * _cache: no-chache_ <br>
 *
 * */

const intellimineUrls = {
    buyLicenseCreateUrl: "/robots/create",
    robotListUrl: "/robots",
    planetsUrl: "/planets",
    robotPathUrl: "/robot/path",
    robotMoveUrl: "/robot/move",
    robotMineUrl: "/robot/mine"
}

const intellimineConstants = {
    MAX_CARGO_AMOUNT: 20,
}

const intellimineDashboardModule = {

    selectedDomainObjects: {
        robot: null,
        planet: null,
        robotsPlanet: null,
        selectedPath: null,
    },

    async fetchPOSTData(url = '', data = {}) {
        // Default options are marked with *
        const response = await fetch(url, {
            method: 'POST', //
            mode: 'same-origin', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                'Content-Type': 'application/json'
            },
            redirect: 'follow', // manual, *follow, error
            referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
            body: JSON.stringify(data) // body data type must match "Content-Type" header
        }).then((response) => {
            this.emit("updateStatus", response);
            return response;
        }).catch((error) => {
            console.error('Error:', error);
        });
        return response.json();
    },

    async fetchGETData(url = '') {
        // Default options are marked with *
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include', // include, *same-origin, omit
            headers: {
                'Content-Type': 'application/json'
            },
        }).then((response) => {
            this.emit("updateStatus", response);
            return response;
        }).catch((error) => {
            console.error('Error:', error);
        });
        return response.json();
    },

    buyNewLicense() {
        this.fetchPOSTData(intellimineUrls.buyLicenseCreateUrl)
            .then(data => {
                this.emit("buyLicense", data);
            })

    },

    selectRobot(robotListElement, robot) {
        if(this.selectedDomainObjects.robot === robot) {
            this.selectedDomainObjects.robot = null;
        } else {
            this.selectedDomainObjects.robot = robot;
        }
        this.emit("robotSelected", robotListElement);
        this.emit("getCalculatedPath");
    },

    selectPlanet(planetDivElement, planet) {
        if(this.selectedDomainObjects.planet === planet) {
            this.selectedDomainObjects.planet = null;
        } else {
            this.selectedDomainObjects.planet = planet;
        }
        this.emit("planetSelected", planetDivElement);
        this.emit("getCalculatedPath");
    },

    moveWithRobot(button, robot) {
        this.emit("move", button, robot);
    },

    updateSelectedRobotAndPlanetAfterMove(robot, steps) {
        this.selectedDomainObjects.planet = null;
        this.fetchGETData(intellimineUrls.robotListUrl).then(robotsAsList => {
            for( let robotElement of robotsAsList) {
                if(robotElement.id === robot.id) {
                    this.selectedDomainObjects.robot = robotElement;
                    this.emit("afterMove", robot, steps);
                }
            }
        })
    },

    mineWithRobot(button, robot) {
        this.emit("mine", button, robot);
    },


    events: {},
    /**
     * Löst ein Ereignis aus. Dieser Funktion dürfen beliebig viele
     * params übergeben werden, diese werden 1:1 an die Event-Listener
     * durchgereicht.
     *
     * @param {string} eventName
     * @param {*=} params
     */
    emit(eventName, param, param2 = "") {
        if (eventName in this.events) {
            for(const f of this.events[eventName]) {
                f(param, param2)
            }
        }
    },

    /**
     * Registriert einen Event-Listener für das Event eventName.
     *
     * @param {string} eventName
     * @param {Function} cb
     */
    on(eventName, cb) {
        if (!(eventName in this.events)) {
            this.events[eventName] = []
        }
        this.events[eventName].push(cb)
    }
}



document.addEventListener("DOMContentLoaded", () => {
    const elements =  {
        buyLicenseButton: document.getElementById("buy-license-btn"),
        listOfRobots: document.getElementById("robot-list"),
        notificationBox: document.getElementById("alert-notification"),
        planetTable: document.getElementById("planets")
    }
    const notifications = {
        error: "Something did not work as expected.",
        licenseBoughtBotCreated: "A new Bot was generated.",
        licenseCantBeBought: "New License can't be created.",
        pathIsIllegal: "Bot can't be moved. Path contains holes.",
        botWasMoved: "Bot was moved to a different planet.",
        minedOnPlanet: "The Bot mined on its current planet.",
        miningOnHomePlanetNotAllowed: "You can't mine on home planet.",
        cargoIsFullReturnHome: "Your cargo is full. Return home to start a new exploration.",

    }


    elements.buyLicenseButton.addEventListener("click", function(e) {
        e.preventDefault();
        intellimineDashboardModule.buyNewLicense();
    });

    function updateStatusMessageText(newText) {
        elements.notificationBox.innerText = newText;
        elements.notificationBox.parentElement.classList.add("new-notification");
        setTimeout(() => {
            elements.notificationBox.parentElement.classList.remove("new-notification");
        },1000);
    }

    function updateStatusMessage(statusCode, responseObject) {
        if(responseObject.status == 200 && responseObject.url.includes("create")) updateStatusMessageText(notifications.licenseBoughtBotCreated);
        if(responseObject.status == 500 && responseObject.url.includes("create")) updateStatusMessageText( notifications.licenseCantBeBought);

    }

    function generateRobotsList() {
        intellimineDashboardModule.fetchGETData(intellimineUrls.robotListUrl)
            .then(data => {
                //console.log(data)
                let robotsAsList = data;
                if(robotsAsList.length > 0) {
                    while(elements.listOfRobots.firstChild) {
                        elements.listOfRobots.removeChild(elements.listOfRobots.lastChild);
                    }
                }
                for(let robot of robotsAsList) {
                    let robotListElement = document.createElement("li");
                    let robotListElementInfoHolder = document.createElement("div");
                    let uuidHolder = document.createElement("span");
                    robotListElement.addEventListener("click", function() {
                        intellimineDashboardModule.selectRobot(robotListElement, robot);
                    });
                    uuidHolder.classList.add("title");
                    uuidHolder.innerText = robot.id;

                    let infoHolder = document.createElement("span");
                    infoHolder.classList.add("info");
                    infoHolder.innerText = "cargo: " + robot.cargoAmount + " | x:" + robot.currentPosition.x + " | y:" + robot.currentPosition.y ;
                    robotListElementInfoHolder.appendChild(uuidHolder);
                    robotListElementInfoHolder.appendChild(infoHolder);
                    robotListElement.appendChild(robotListElementInfoHolder);

                    let robotListElementButtonHolder = document.createElement("div");
                    robotListElementButtonHolder.classList.add("actions");
                    let robotListMoveButton = document.createElement("button");
                    let robotListMineButton = document.createElement("button");
                    robotListMoveButton.classList.add("move");
                    robotListMineButton.classList.add("mine");
                    robotListMoveButton.innerText = "MOVE";
                    robotListMineButton.innerText = "MINE";

                    robotListElementButtonHolder.appendChild(robotListMoveButton);
                    robotListElementButtonHolder.appendChild(robotListMineButton);

                    robotListMoveButton.addEventListener("click", function(e) {
                        e.stopPropagation();
                        intellimineDashboardModule.moveWithRobot(robotListMoveButton, robot);
                    });
                    robotListMineButton.addEventListener("click", function(e) {
                        e.stopPropagation();
                        intellimineDashboardModule.mineWithRobot(robotListMineButton, robot);
                    });

                    robotListElement.appendChild(robotListElementButtonHolder);
                    elements.listOfRobots.appendChild(robotListElement);
                }
            })
    }

    function generateMap() {
        return intellimineDashboardModule.fetchGETData(intellimineUrls.planetsUrl)
            .then(data => {
                let planetList = data.planets;
                let minY = data.minY;
                let maxY = data.maxY;
                let minX = data.minX;
                let maxX = data.maxX;

                let planetTable = elements.planetTable;
                while(planetTable.firstChild) {
                    planetTable.removeChild(planetTable.lastChild);
                }
                for(let y = minY; y <= maxY; y++) {
                    let tr = document.createElement("tr");
                    for(let x = minX; x <= maxX; x++) {
                        let td = document.createElement("td");
                        td.setAttribute("data-x", x);
                        td.setAttribute("data-y", y);
                        td.id = "x" + x +"y" + y;
                        if(y == 0 && x == 0) td.classList.add("space-shipyard");
                        tr.appendChild(td);
                    }
                    planetTable.appendChild(tr);
                }

                for(let planet of planetList) {
                    let x = planet.coordinate.x;
                    let y = planet.coordinate.y;
                    //let tableCell = document.querySelector("td[data-x='${x}'][data-y='${y}']");
                    let tableCell = document.getElementById("x" + x + "y" + y);
                    let planetDivElement = document.createElement("div");
                    planetDivElement.setAttribute("data-resource", planet.resourceAmount);
                    planetDivElement.setAttribute("data-uuid", planet.id);

                    planetDivElement.classList.add("planet");
                    planetDivElement.addEventListener("click", function() {
                        intellimineDashboardModule.selectPlanet(planetDivElement, planet);
                    });
                    tableCell.appendChild(planetDivElement);
                }
            })
    }

    function updateResourcesOnMap(robotCargo) {

        let planets = document.querySelectorAll(".planet");
        for(let planet of planets) {
            if(planet.parentNode.classList.contains("space-shipyard")) continue;
            let resourceAmount = planet.dataset.resource;
            planet.innerText = resourceAmount;
            if(robotCargo !== intellimineConstants.MAX_CARGO_AMOUNT && resourceAmount >= (intellimineConstants.MAX_CARGO_AMOUNT - robotCargo)) {
                planet.classList.add("max-mining-planet");
            }
        }
    }

    function updateSelectedRobotOnMap() {
        let planets = document.querySelectorAll(".planet");
        for(let planet of planets) {
            planet.classList.remove("selected-robot-position");
            let x = parseInt(planet.parentNode.dataset.x);
            let y = parseInt(planet.parentNode.dataset.y);
            let selectedRobot = intellimineDashboardModule.selectedDomainObjects.robot;
            if(selectedRobot !== null) {
                if (x === selectedRobot.currentPosition.x &&
                    y === selectedRobot.currentPosition.y) {
                    planet.classList.add("selected-robot-position");
                }
            }
        }
    }


    function removePathOnMap(planetHolders) {
        for(let planetHolder of planetHolders) {
            planetHolder.classList.remove("highlight-path");
        }
    }

    function showPathOnMap(steps) {
        let planetHolders = document.querySelectorAll("td");
        removePathOnMap(planetHolders)
        for(let planetHolder of planetHolders) {
            let x = parseInt(planetHolder.dataset.x);
            let y = parseInt(planetHolder.dataset.y);
            for(let step of steps) {
                if (x === step.x &&
                    y === step.y) {
                    planetHolder.classList.add("highlight-path");
                }
            }

        }
    }

    function fadeShownPathOnMapOut(steps) {
        showPathOnMap(steps);
        for(let step of steps) {
            let fittingPlanetHolder = document.getElementById("x" + step.x + "y" + step.y);
            setTimeout(() => {
                fittingPlanetHolder.classList.remove("highlight-path");
            },1000);
        }

    }


    intellimineDashboardModule.on("buyLicense", (data) => {
        generateRobotsList();
    })

    intellimineDashboardModule.on("updateStatus", (response) => {
        updateStatusMessage(response.status, response);
    })

    intellimineDashboardModule.on("robotSelected", (robotListElement) => {
        if(robotListElement.classList.contains("selected")) {
            robotListElement.classList.remove("selected");
            let planetHolders = document.querySelectorAll("td");
            removePathOnMap(planetHolders);
            updateResourcesOnMap(0);
            updateSelectedRobotOnMap();
            return;
        }
        let robotList = robotListElement.parentElement.children;
        for(let robotListElement of robotList) {
            robotListElement.classList.remove("selected");
        }
        robotListElement.classList.add("selected");
        updateResourcesOnMap(intellimineDashboardModule.selectedDomainObjects.robot.cargoAmount);
        updateSelectedRobotOnMap();
    })

    intellimineDashboardModule.on("planetSelected", (planetDivElement) => {
        if(planetDivElement.classList.contains("selected")) {
            planetDivElement.classList.remove("selected");
            let planetHolders = document.querySelectorAll("td");
            removePathOnMap(planetHolders);
            return;
        }
        let planetList = document.querySelectorAll(".planet");
        for(let planetListElement of planetList) {
            planetListElement.classList.remove("selected");
        }
        planetDivElement.classList.add("selected");
    })

    intellimineDashboardModule.on("getCalculatedPath", () => {
        let selectedRobot = intellimineDashboardModule.selectedDomainObjects.robot;
        let selectedPlanet = intellimineDashboardModule.selectedDomainObjects.planet;

        if( selectedRobot === null ||
            selectedPlanet === null) {
            return;
        }

        if( selectedRobot.currentPosition === selectedPlanet.coordinate) {
            return;
        }

        let getPathUrl = intellimineUrls.robotPathUrl + "?robotId=" + selectedRobot.id + "&targetPlanetId=" + selectedPlanet.id;
        intellimineDashboardModule.fetchGETData(getPathUrl)
            .then(response => {
                showPathOnMap(response.steps);
                intellimineDashboardModule.selectedDomainObjects.selectedPath = response;
            })
    })


    intellimineDashboardModule.on("move", (button, robot) => {
        const selectedPath = intellimineDashboardModule.selectedDomainObjects.selectedPath;
        if(selectedPath == null) return;
        if (!selectedPath.legal) {
            updateStatusMessageText(notifications.pathIsIllegal);
            return;
        }

        intellimineDashboardModule.fetchPOSTData(intellimineUrls.robotMoveUrl, intellimineDashboardModule.selectedDomainObjects.selectedPath).then(response => {
            // console.log(response);
            updateDashboard(robot.cargoAmount).then(() => {
                intellimineDashboardModule.updateSelectedRobotAndPlanetAfterMove(robot, response.steps);
            });
        })
    })

    intellimineDashboardModule.on("afterMove", (robot, steps) => {
        updateStatusMessageText(notifications.botWasMoved);
        fadeShownPathOnMapOut(steps);
        updateSelectedRobotOnMap();
    });


    intellimineDashboardModule.on("mine", (button, robot) => {
        if(robot.currentPosition.x === 0 && robot.currentPosition.y === 0) {
            updateStatusMessageText(notifications.miningOnHomePlanetNotAllowed);
            return;
        }
        if(robot.cargoAmount >= intellimineConstants.MAX_CARGO_AMOUNT) {
            updateStatusMessageText(notifications.cargoIsFullReturnHome);
            return;
        }

        let miningUrl = intellimineUrls.robotMineUrl + "?robotId=" + robot.id;

        intellimineDashboardModule.fetchGETData(miningUrl).then(response => {
            console.log(response);
            updateStatusMessageText(notifications.minedOnPlanet);
            updateDashboard(robot.cargoAmount);
        })




    })


    const resetSavedDomainObjectsInModule = () => {
        // Reset
        intellimineDashboardModule.selectedDomainObjects.planet = null;
        intellimineDashboardModule.selectedDomainObjects.robot = null;
        intellimineDashboardModule.selectedDomainObjects.selectedPath = null;
    }

    /**
    *  Updates the Dashboard
    * */
    const updateDashboard = (robotCargo = 0) => {
        generateRobotsList();
        return generateMap().then(() => {
            updateResourcesOnMap(robotCargo);
            updateSelectedRobotOnMap();
        });


    }


    // List all Eventlisteners on which Dashboard should be updated
    intellimineDashboardModule.on("buyLicense", updateDashboard);

    resetSavedDomainObjectsInModule();
    updateDashboard();

})
let apiURLs = {
  "apiSpam": "http://localhost:8080/spamDetector-1.0/api/spam",
  "apiPrecision": "http://localhost:8080/spamDetector-1.0/api/spam/precision",
  "apiAccuracy": "http://localhost:8080/spamDetector-1.0/api/spam/accuracy",
}

function setAccuracy(accuracy) {
  const element = document.querySelector("#div-accuracy");
  element.innerHTML = accuracy;
}

function setPrecision(precision) {
  const element = document.querySelector("#div-precision");
  element.innerHTML = precision
}

function createNewRow(file, probability, classtype) {
  const row = document.createElement("tr");
  const td_file = document.createElement("td");
  const td_probability = document.createElement("td");
  const td_classtype = document.createElement("td");

  td_file.textContent = file;
  td_probability.textContent = probability;
  td_classtype.textContent = classtype;

  row.appendChild(td_file);
  row.appendChild(td_probability);
  row.appendChild(td_classtype);

  const tableBody = document.querySelector("#data-table tbody");
  tableBody.appendChild(row)
}

function parseFileJson(data_to_parse) {
  console.log(data_to_parse);
  for (let data of data_to_parse) {
    console.log(data)
    createNewRow(data.file, data.spamProbability, data.actualClass);
  }
}

function parsePrecisionJson(data_to_parse) {
  console.log(data_to_parse);
  setPrecision(data_to_parse.val);
}

function parseAccuracyJson(data_to_parse) {
  console.log(data_to_parse);
  setAccuracy(data_to_parse.val);
}

function requestsDataFromServer(url, callbackFunction) {
  fetch(url, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }
  }).then(response => response.json())
    .then(response => callbackFunction(response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });
}


(function () {
  requestsDataFromServer(apiURLs.apiSpam, parseFileJson);
  requestsDataFromServer(apiURLs.apiPrecision, parsePrecisionJson);
  requestsDataFromServer(apiURLs.apiAccuracy, parseAccuracyJson);
})();

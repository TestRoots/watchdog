const timeseriesAnalysis = require('timeseries-analysis');

const fs = require('fs');
const BSONStream = require('bson-stream');

const usersIpsWithIdes = {};
const eventTypeCounts = {
  ['sa-wc']: 0,
  ['sa-wr']: 0
};
const eventTypeForUsers = {};
const timestampsPerUser = {};

fs.createReadStream('events.bson')
  .pipe(new BSONStream())
  .on('data', processEvent)
  .on('end', reportStatistics);

function processEvent(event) {
  if (event.et !== 'sa-wc' && event.et !== 'sa-wr') {
    return;
  }

  const userId = event.userId;

  if (usersIpsWithIdes[userId]) {
    if (usersIpsWithIdes[userId].indexOf(event.ide) === -1) {
      usersIpsWithIdes[userId].push(event.ide);
    }
  } else {
    usersIpsWithIdes[userId] = [event.ide];
  }

  eventTypeCounts[event.et]++;

  if (!eventTypeForUsers[userId]) {
    eventTypeForUsers[userId] = {
      ['sa-wc']: 0,
      ['sa-wr']: 0
    };
  }
  eventTypeForUsers[userId][event.et]++;

  const unixTimestamp = Math.round(event.ts/1000);

  if (!timestampsPerUser[userId]) {
    timestampsPerUser[userId] = {};
  }

  if (!timestampsPerUser[userId][unixTimestamp]) {
    timestampsPerUser[userId][unixTimestamp] = 0;
  }
  timestampsPerUser[userId][unixTimestamp] += event.et === 'sa-wc' ? 1 : -1;
};

function reportStatistics() {
  console.log(usersIpsWithIdes);
  console.log(eventTypeCounts);
  console.log(eventTypeForUsers);
  console.log(timestampsPerUser);

  const timeseries = new timeseriesAnalysis.main([]);
  for (const user of Object.keys(timestampsPerUser)) {
    let previousValue = 0;
    timeseries.buffer = Object.entries(timestampsPerUser[user]).sort().map(([date, count]) => {
      previousValue += count;
      return [date, previousValue];
    });
    timeseries.data = timeseries.buffer;
    timeseries.save(user).reset();
  }
  console.log(timeseries.chart({main: true, lines: [0]}));
}

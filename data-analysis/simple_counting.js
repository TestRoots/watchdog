const timeseriesAnalysis = require('timeseries-analysis');

const output = require('./results.json');

const usersIpsWithIdes = {};
const eventTypeCounts = {
  ['sa-wc']: 0,
  ['sa-wr']: 0
};
const eventTypeForUsers = {};
const timestampsPerUser = {};

output.forEach(event => {
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

  const unixTimestamp = Math.round(parseInt(event.ts['$numberLong'])/1000);

  if (!timestampsPerUser[userId]) {
    timestampsPerUser[userId] = {};
  }

  if (!timestampsPerUser[userId][unixTimestamp]) {
    timestampsPerUser[userId][unixTimestamp] = 0;
  }
  timestampsPerUser[userId][unixTimestamp] += event.et === 'sa-wc' ? 1 : -1;
});

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

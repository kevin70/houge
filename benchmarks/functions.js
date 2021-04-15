module.exports = { createTestMessage };

function createTestMessage(userContext, events, done) {
  var to = Math.floor(Math.random() * 100000) + 1;

  const message = {
    "@ns": "message",
    to: 2,
    content: `Hello [${to}]!`,
  };

  userContext.vars.testMessage = message;
  return done();
}

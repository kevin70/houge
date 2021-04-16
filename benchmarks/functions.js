module.exports = { createTestMessage };

function createTestMessage(userContext, events, done) {
  // const to = Math.floor(Math.random() * 100000) + 1;
  const to = 100;

  const message = {
    "@ns": "message",
    kind: 1,
    to: to,
    content: `Hello [${to}]!`,
  };

  userContext.vars.testMessage = message;
  return done();
}

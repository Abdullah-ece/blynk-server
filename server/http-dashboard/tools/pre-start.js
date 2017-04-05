const fs = require('fs-extra');
const path = require('path');

try {
  fs.copySync(path.resolve(__dirname, './help/git-pre-commit-hook'), __dirname + '/../../../.git/hooks/pre-commit');
} catch (e) {
  process.exit(1);
}

process.exit(0);

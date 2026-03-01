const { spawn } = require("child_process");
const path = require("path");

const isWindows = process.platform === "win32";
const backendPath = path.join(__dirname, "back");
console.log("==== Lancement du backend Spring Boot en profil TEST... ====");

const backend = spawn(
  isWindows ? "mvn.cmd" : "mvn",
  ["spring-boot:run", "-Dspring-boot.run.profiles=test"],
  { cwd: backendPath, stdio: "inherit" }
);

const frontendPath = path.join(__dirname, "front");
console.log("==== Installation des dépendances frontend... ====");
const npmInstall = spawn(isWindows ? "npm.cmd" : "npm", ["install"], {
  cwd: frontendPath,
  stdio: "inherit",
});

npmInstall.on("close", (code) => {
  if (code !== 0) {
    console.error("Erreur lors de l'installation npm.");
    process.exit(code);
  }

  console.log("==== Lancement du frontend Angular... ====");
  const frontend = spawn(isWindows ? "npm.cmd" : "npm", ["start"], {
    cwd: frontendPath,
    stdio: "inherit",
  });

  frontend.on("close", () => {
    console.log("Frontend arrêté. Arrêt du backend...");
    backend.kill();
    process.exit(0);
  });
});

process.on("SIGINT", () => {
  console.log("CTRL+C reçu. Arrêt des processus...");
  backend.kill();
  process.exit(0);
});
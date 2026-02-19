// Runs Spring boot and Angular


const { spawn } = require("child_process");
const path = require("path");

const isWindows = process.platform === "win32";

// Lancer le backend Spring Boot
const backendPath = path.join(__dirname, "back");
console.log("======= Lancement de Spring Boot en profil TEST port 8080 =======\n\n");

const backend = spawn(
  isWindows ? "mvn.cmd" : "mvn",
  ["spring-boot:run", "-Dspring-boot.run.profiles=test"],
  { cwd: backendPath, stdio: "inherit" }
);

const frontendPath = path.join(__dirname, "front");

// Installer les dépendances
console.log("======= Installation dépendances Angular  =======\n\n");
const npmInstall = spawn(isWindows ? "npm.cmd" : "npm", ["install"], {
  cwd: frontendPath,
  stdio: "inherit",
});

npmInstall.on("close", (code) => {
  if (code !== 0) {
    console.error("Erreur lors de l'installation npm.");
    process.exit(code);
  }

  console.log(" ======= Lancement Angular port 4200 =======\n\n");
  const frontend = spawn(isWindows ? "npm.cmd" : "npm", ["start"], {
    cwd: frontendPath,
    stdio: "inherit",
  });

  // Quand le frontend s'arrête, tuer le backend
  frontend.on("close", () => {
    console.log("Frontend arrêté. Arrêt du backend...");
    backend.kill();
    process.exit(0);
  });
});

// Quand on interrompt le script, tuer backend et frontend
process.on("SIGINT", () => {
  console.log("CTRL+C reçu. Arrêt des processus...");
  backend.kill();
  process.exit(0);
});

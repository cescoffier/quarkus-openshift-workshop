import { useState, useEffect } from 'preact/hooks'
// import preactLogo from './assets/preact.svg'
// import viteLogo from '/vite.svg'
import './app.css'

import backLogo from './assets/quarkus_icon_black.svg';

function createFighterData(role) {
  return {
    "role": role,
    "ready": false,
    "info": {
      "id": 0,
      "level": 1,
      "name": "N/A",
      "otherName": "N/A",
      "picture": backLogo,
      "powers": "N/A"
    }
  }
}

export function App() {

  const [heroData, setHeroData] = useState(createFighterData("Hero"));
  const [villainData, setVillainData] = useState(createFighterData("Villain"));
  const [actionEnabled, setActionEnabled] = useState(true);

  const reloadFighter = (backend, data, updater) => {

    updater({
      "role": data.role,
      "ready": false,
      "info": data.info
    });

    return fetch(backend)
      .then(response => {
        if (response.ok) {
          response.json().then(infoData => {
            const update = {
              "role": data.role,
              "ready": true,
              "info": infoData
            };
            updater(update);
            return update;
          });
        }
      })
      .catch(err => console.error(`Fetch to ${backend} failed: ${err}`));
  };

  const reloadFighters = () => {
    setActionEnabled(false);
    return Promise.all([
      reloadFighter("http://localhost:8080/api/heroes/random", heroData, setHeroData),
      reloadFighter("http://localhost:8081/api/villains/random", villainData, setVillainData)
    ]).finally(() => {
      setActionEnabled(true);
    });
  };

  useEffect(() => {
    reloadFighters();
  }, []);

  return (
    <>
      <main class="container-fluid">
        <div class="grid">
          <Fighter data={heroData} />
          <ActionBox reloadAction={() => reloadFighters()} actionEnabled={actionEnabled} />
          <Fighter data={villainData} />
        </div>
      </main>
    </>
  )
}

function Fighter(props) {

  return (
    <>
      <div>
        <article>
          <header>
            <FighterHeader data={props.data} />
          </header>
          <p>
            <img class="fighter-pic" src={props.data.info.picture} />
          </p>
          <hgroup>
            <h3>{props.data.info.name} (Level {props.data.info.level})</h3>
            <p>
              <strong>Powers</strong> &mdash; <small><em>{props.data.info.powers}</em></small>
            </p>
          </hgroup>
        </article>
      </div>
    </>
  )
}

function FighterHeader(props) {
  if (props.data.ready) {
    return (
      <>
        <h1>{props.data.role}</h1>
      </>
    )
  } else {
    return (
      <>
        <h1>{props.data.role} <span aria-busy="true"></span></h1>
      </>
    )
  }
}

function ActionBox(props) {

  return (
    <>
      <div>
        <article>
          <header>
            <h1 class="centered">&mdash; VS &mdash;</h1>
          </header>
          <ActionButtons reloadAction={props.reloadAction} actionEnabled={props.actionEnabled} />
        </article>
      </div>
    </>
  )
}

function ActionButtons(props) {

  if (props.actionEnabled) {
    return (
      <>
        <div class="grid">
          <button>Fight!</button>
          <button class="secondary" onClick={props.reloadAction}>Reload</button>
        </div>
      </>
    )
  } else {
    return (
      <>
        <div class="grid">
          <button disabled>Fight!</button>
          <button disabled class="secondary" onClick={props.reloadAction}>Reload</button>
        </div>
      </>
    )
  }
}

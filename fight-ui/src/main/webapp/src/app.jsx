import { useState, useEffect } from 'preact/hooks'
import '@picocss/pico/css/pico.red.min.css';
import './app.css'

import backLogo from './assets/quarkus_icon_black.svg';

import configuration from './config.js';

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

function initState() {
  return {
    "state": "init",
  };
}

function loadingState() {
  return {
    "state": "loading",
  };
}

function fightingState() {
  return {
    "state": "fighting",
  };
}

function resultState(result) {
  return {
    "state": "result",
    "result": result
  };
}

function errorState(message) {
  return {
    "state": 'init',
    "error": message
  }
}

function fixWinner(hero, winner) {
  if (winner != 'Hero' && winner != 'Villain') {
    return (winner == hero) ? 'Hero' : 'Villain';
  }
  return winner;
}

export function App() {

  const [heroData, setHeroData] = useState(createFighterData("Hero"));
  const [villainData, setVillainData] = useState(createFighterData("Villain"));
  const [appState, setAppState] = useState(loadingState());

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
        } else {
          throw new Error(`Fetch to ${backend} failed with status code ${response.status}`);
        }
      })
      .catch(err => {
        throw new Error(`${data.role} API failure: ${err.message}`);
      });
  };

  const reloadFighters = () => {
    setAppState(loadingState());
    return Promise.all([
      reloadFighter(configuration.endpoints.heroes, heroData, setHeroData),
      reloadFighter(configuration.endpoints.villains, villainData, setVillainData)
    ])
      .then(() => {
        setAppState(initState());
      })
      .catch(err => {
        setAppState(errorState(err.message));
      });
  };

  const fight = () => {
    setAppState(fightingState());
    const fightPayload = {
      "hero": {
        "name": heroData.info.name,
        "level": heroData.info.level,
        "powers": heroData.info.powers.split(", ")
      },
      "villain": {
        "name": villainData.info.name,
        "level": villainData.info.level,
        "powers": villainData.info.powers.split(", ")
      }
    }
    fetch(configuration.endpoints.fight, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(fightPayload)
    })
      .then(response => {
        if (response.ok) {
          response.json().then(result => {
            result.winner = fixWinner(heroData.info.name, result.winner);
            setAppState(resultState(result));
          });
        } else {
          throw new Error(`Fight API failed with status code ${response.status}`);
        }
      })
      .catch(err => {
        setAppState(errorState(`Fight API error: ${err.message}`));
      });
  };

  useEffect(() => {
    reloadFighters();
  }, []);

  return (
    <>
      <main class="container-fluid">
        <div class="grid">
          <Fighter data={heroData} appState={appState} />
          <ActionBox fightAction={() => fight()} reloadAction={() => reloadFighters()} appState={appState} heroData={heroData} villainData={villainData} />
          <Fighter data={villainData} appState={appState} />
        </div>
      </main>
    </>
  )
}

function Fighter({ appState, data }) {

  let resultClass = "";
  if (appState.state == 'result') {
    if (appState.result.winner == data.role) {
      resultClass = "winner-box";
    } else {
      resultClass = "looser-box";
    }
  }
  resultClass = resultClass + ' ' + ((data.role == 'Hero') ? 'hero-card' : 'villain-card');
  const imageClass = (data.role == 'Hero') ? 'fighter fighter-pic hero-pic' : 'fighter fighter-pic villain-pic';

  return (
    <>
      <div class={resultClass}>
        <article>
          <header>
            <FighterHeader data={data} appState={appState} />
          </header>
          <p>
            <img class={imageClass} src={data.info.picture} />
          </p>
          <hgroup>
            <h3>{data.info.name} (Level {data.info.level})</h3>
            <p>
              <strong>Powers</strong> &mdash; <small><em>{data.info.powers}</em></small>
            </p>
          </hgroup>
        </article>
      </div>
    </>
  )
}

function FighterHeader({ appState, data }) {
  if (appState.state != 'fighting' && appState.state != 'loading') {
    return (
      <>
        <h1>{data.role}</h1>
      </>
    )
  } else {
    return (
      <>
        <h1>{data.role} <span aria-busy="true"></span></h1>
      </>
    )
  }
}

function ActionBox({ appState, fightAction, reloadAction, heroData, villainData }) {

  return (
    <>
      <div>
        <article>
          <header>
            <h1 class="centered">&mdash; VS &mdash;</h1>
          </header>
          <ErrorBox appState={appState} />
          <ActionButtons appState={appState} fightAction={fightAction} reloadAction={reloadAction} />
          <footer>
            <NarrationArea appState={appState} heroData={heroData} villainData={villainData} />
          </footer>
        </article>
      </div>
    </>
  )
}

function ErrorBox({ appState }) {
  if (!appState.hasOwnProperty('error')) {
    return null;
  }
  return (
    <>
      <div class="error-box">
        <h5>Error</h5>
        <p>
          <small>{appState.error}</small>
        </p>
      </div>
      <hr />
    </>
  )
}

function ActionButtons({ appState, reloadAction, fightAction }) {

  if (appState.state == 'init') {
    return (
      <>
        <div class="grid">
          <button onClick={fightAction}>Fight!</button>
          <button class="secondary" onClick={reloadAction}>Reload</button>
        </div>
      </>
    )
  } else if (appState.state == 'fighting' || appState.state == 'loading') {
    return (
      <>
        <div class="grid">
          <button disabled>Fight!</button>
          <button disabled class="secondary">Reload</button>
        </div>
      </>
    )
  } else {
    return (
      <>
        <div class="grid">
          <button disabled>Fight!</button>
          <button class="secondary" onClick={reloadAction}>Reload</button>
        </div>
      </>
    )
  }
}

function NarrationArea({ appState, heroData, villainData }) {
  if (appState.state == 'fighting') {
    return (
      <>
        <progress />
      </>
    )
  } else if (appState.state != 'result') {
    return null;
  } else {
    const hero = extractShorterName(heroData.info.name);
    const villain = extractShorterName(villainData.info.name);
    const winner = appState.result.winner;
    const fancy = {
      __html: appState.result.narration
        .replaceAll(nameRegexp(hero), `<mark class="${winner == 'Hero' ? 'winner-mark' : 'looser-mark'}">${hero}</mark>`)
        .replaceAll(nameRegexp(villain), `<mark class="${winner == 'Villain' ? 'winner-mark' : 'looser-mark'}">${villain}</mark>`)
    };
    return (
      <>
        <h3>Story of the fight</h3>
        <p>
          <cite>
            "<span dangerouslySetInnerHTML={fancy} />"
          </cite>
        </p>
      </>
    )
  }
}

function nameRegexp(name) {
  const firstName = name.split(' ')[0];
  return new RegExp(`(${name}|${firstName})`, 'gi');
}

function extractShorterName(name) {
  const parens = /\(.+\)/gi;
  return name.replaceAll(parens, '').trim();
}

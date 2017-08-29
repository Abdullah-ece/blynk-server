import React            from 'react';
import {Plotly}         from 'components';
// import Highlight        from 'react-highlight';

class BackTopBook extends React.Component {

  render() {

    const trace1 = {
      x: [1, 3, 5, 10],
      y: [1, 2, 3, 4],
      type: 'scatter'
    };

    const trace2 = {
      x: [1, 2, 3, 4],
      y: [16, 5, 11, 9],
      type: 'scatter',
      yaxis: 'y2'
    };

    const data = [trace1, trace2];
    const layout = {
      width: 500,
      height: 200,
      margin: {
        l: 0,
        r: 0,
        t: 0,
        b: 0,
      },
      yaxis: {
        title: 'Title 1'
      },
      yaxis2: {
        overlaying: 'y',
        titlefont: {color: 'rgb(148, 103, 189)'},
        tickfont: {color: 'rgb(148, 103, 189)'},
        title: 'Title 2',
        side: 'right'
      }
    };

    const config = {
      displayModeBar: false
    };

    return (
      <div>

        <h4>Example</h4>

        <Plotly data={data} layout={layout} config={config}/>

      </div>
    );
  }

}

export default BackTopBook;

import React            from 'react';
import {BackTop}        from 'components';
import Highlight        from 'react-highlight';

class BackTopBook extends React.Component {

  render() {
    return (
      <div>

        <h4>Example</h4>

        <BackTop badgeCount={5} visibilityHeight={-1}/>

        <h4>Code</h4>

        <Highlight>
          {`<BackTop badgeCount={5} visibilityHeight={-1} />`}
        </Highlight>

      </div>
    );
  }

}

export default BackTopBook;

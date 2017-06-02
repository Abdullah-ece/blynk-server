import React from 'react';
import Highlight from 'react-highlight';
import {Section} from 'components';

class SectionBook extends React.Component {

  render() {
    return (
      <div>

        <h4>Example</h4>

        <Section title="Example title">
          Example content
        </Section>

        <h4>Code</h4>

        <Highlight>
          {`<Section title="Example title">
  Example content
</Section>`}
        </Highlight>

      </div>
    );
  }

}

export default SectionBook;

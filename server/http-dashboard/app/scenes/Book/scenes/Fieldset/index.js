import React from 'react';
import Highlight from 'react-highlight';
import {Fieldset} from 'components';

class FieldsetBook extends React.Component {

  render() {
    return (
      <div>

        <h4>Example</h4>

        <Fieldset>
          <Fieldset.Legend>User</Fieldset.Legend>
          Some text is there
        </Fieldset>

        <h4>Code</h4>

        <Highlight>
          {`<Fieldset>
 <Fieldset.Legend>User</Fieldset.Legend>
 Some text is there
</Fieldset>`}
        </Highlight>

        <h4>Example</h4>

        <Fieldset>
          <Fieldset.Legend type="dark">User</Fieldset.Legend>
          Some text is there
        </Fieldset>

        <h4>Code</h4>

        <Highlight>
          {`<Fieldset>
 <Fieldset.Legend type="dark">User</Fieldset.Legend>
 Some text is there
</Fieldset>`}
        </Highlight>

      </div>
    );
  }

}

export default FieldsetBook;
